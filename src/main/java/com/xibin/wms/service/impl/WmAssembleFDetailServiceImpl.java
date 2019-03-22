package com.xibin.wms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.ComputeUtil;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmActTranMapper;
import com.xibin.wms.dao.WmAssembleFDetailMapper;
import com.xibin.wms.dao.WmAssembleSDetailMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmAssembleFDetail;
import com.xibin.wms.pojo.WmAssembleHeader;
import com.xibin.wms.pojo.WmAssembleSDetail;
import com.xibin.wms.query.WmAssembleFDetailQueryItem;
import com.xibin.wms.service.WmAssembleFDetailService;
import com.xibin.wms.service.WmAssembleHeaderService;
import com.xibin.wms.service.WmAssembleSDetailService;
import com.xibin.wms.service.WmInventoryService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmAssembleFDetailServiceImpl extends BaseManagerImpl implements WmAssembleFDetailService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmAssembleHeaderService wmAssembleHeaderService;
	@Resource
	private WmAssembleFDetailMapper wmAssembleFDetailMapper;
	@Resource
	private WmAssembleSDetailMapper wmAssembleSDetailMapper;
	@Resource
	private WmActTranMapper wmActTranMapper;
	@Resource
	private WmAssembleSDetailService wmAssembleSDetailService;
	@Resource
	private WmInventoryService wmInventoryService;

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmAssembleFDetailMapper;
	}

	@Override
	public WmAssembleFDetail getAssembleOrderById(int id) {
		// TODO Auto-generated method stub
		return wmAssembleFDetailMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmAssembleFDetailQueryItem> getAllAssembleFDetailByOrderNo(Map map) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId());
		map.put("warehouseId", userDetails.getWarehouseId());
		return wmAssembleFDetailMapper.selectAllByOrderNo(map);
	}

	@Override
	public List<WmAssembleFDetail> selectByKey(String orderNo, String lineNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return wmAssembleFDetailMapper.selectByKey(orderNo, lineNo, userDetails.getCompanyId().toString(),
				userDetails.getWarehouseId().toString());
	}

	@Override
	public List<WmAssembleFDetail> selectByExample(WmAssembleFDetail model) {
		// TODO Auto-generated method stub
		return wmAssembleFDetailMapper.selectByExample(model);
	}

	@Override
	public WmAssembleFDetail saveAssembleFDetail(WmAssembleFDetail model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if (null == model.getId() || 0 == model.getId()) {
			List<Integer> lastLineNo = wmAssembleFDetailMapper.selectLastLineNo(model.getOrderNo(),
					model.getCompanyId().toString(), model.getWarehouseId().toString());
			Integer num = lastLineNo.get(0);
			if (num == null) {
				model.setLineNo("1");
			} else {
				num++;
				model.setLineNo(num + "");
			}
		}

		this.save(model);
		List<WmAssembleFDetail> list = selectByKey(model.getOrderNo(), model.getLineNo());
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public int remove(String orderNo, String lineNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Message message = operateBeforeCheck(orderNo);

		if (message.getCode() == 0) {
			throw new BusinessException(message.getMsg());
		} else {
			List<WmAssembleFDetail> list = selectByKey(orderNo, lineNo);
			if (list.size() > 0) {
				Integer idInteger = list.get(0).getId();
				return this.delete(idInteger);
			} else {
				return 0;
			}
		}
	}

	@Override
	public Message assemble(String orderNo, String lineNo, double assembleNum) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmAssembleFDetail> fDetails = this.selectByKey(orderNo, lineNo);
		if (fDetails.size() == 0) {
			throw new BusinessException("组装单[" + orderNo + "]行号[" + lineNo + "]父件明细不存在，请联系管理员");
		}
		WmAssembleFDetail fDetail = fDetails.get(0);
		double noAssembleNum = ComputeUtil.sub(fDetail.getPreNum().doubleValue(), fDetail.getNum().doubleValue());
		if (noAssembleNum < assembleNum) {
			throw new BusinessException("加工数（" + assembleNum + "）大于未加工数（" + noAssembleNum + "）");
		}
		WmAssembleSDetail sDetailQueryExample = new WmAssembleSDetail();
		sDetailQueryExample.setOrderNo(orderNo);
		sDetailQueryExample.setfLineNo(lineNo);
		sDetailQueryExample.setCompanyId(userDetails.getCompanyId());
		sDetailQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmAssembleSDetail> sDetails = wmAssembleSDetailService.selectByExample(sDetailQueryExample);
		List<Double> qtyOps = new ArrayList<Double>();
		List<WmActTran> actTrans = new ArrayList<WmActTran>();
		double totalCost = 0.0;
		for (WmAssembleSDetail sDetail : sDetails) {
			double perNum = ComputeUtil.div(sDetail.getNum(), fDetail.getPreNum(), 2);
			double needNum = ComputeUtil.mul(perNum, assembleNum);
			double haveNum = ComputeUtil.sub(sDetail.getPickNum().doubleValue(),
					sDetail.getAssembleNum().doubleValue());
			if (needNum > haveNum) {
				throw new BusinessException("子件[" + sDetail.getFittingSkuCode() + "]拣货数量不足");
			}
			qtyOps.add(needNum);
			// 数量足够的话
			sDetail.setAssembleNum(ComputeUtil.add(sDetail.getAssembleNum().doubleValue(), needNum));
		}
		for (int i = 0; i < sDetails.size(); i++) {
			WmActTran sDetailActTran = updateSDetailInventory(sDetails.get(i), qtyOps.get(i));
			totalCost = ComputeUtil.add(totalCost, sDetailActTran.getCost());
			actTrans.add(sDetailActTran);
		}
		DaoUtil.save(sDetails, wmAssembleSDetailMapper, session);
		fDetail.setNum(ComputeUtil.add(fDetail.getNum().doubleValue(), assembleNum));
		if (fDetail.getNum().doubleValue() == fDetail.getPreNum().doubleValue()) {
			fDetail.setStatus(WmsCodeMaster.ASS_FULL.getCode());
		} else {
			fDetail.setStatus(WmsCodeMaster.ASS_PART.getCode());
		}
		WmActTran fDetailActTran = updateFDetailInventory(fDetail, assembleNum, totalCost);
		actTrans.add(fDetailActTran);
		saveAssembleFDetail(fDetail);
		DaoUtil.save(actTrans, wmActTranMapper, session);
		WmAssembleHeader header = updateHeaderStatus(orderNo);
		message.setData(header);
		message.setCode(200);
		message.setMsg("操作成功");
		return message;
	}

	private WmActTran updateSDetailInventory(WmAssembleSDetail sDetail, double qtyOp) throws BusinessException {
		InventoryUpdateEntity entity = new InventoryUpdateEntity();
		entity.setActionCode(WmsCodeMaster.ACT_ASSEMBLE_S.getCode());
		entity.setLineNo(sDetail.getLineNo());
		entity.setLocCode(sDetail.getAssembleLoc());
		entity.setOrderNo(sDetail.getOrderNo());
		entity.setOrderType(WmsCodeMaster.ORDER_ASS.getCode());
		entity.setQtyOp(qtyOp);
		entity.setSkuCode(sDetail.getFittingSkuCode());
		return wmInventoryService.updateInventory(entity);
	}

	private WmActTran updateFDetailInventory(WmAssembleFDetail fDetail, double qtyOp, double cost)
			throws BusinessException {
		InventoryUpdateEntity entity = new InventoryUpdateEntity();
		entity.setActionCode(WmsCodeMaster.ACT_ASSEMBLE_F.getCode());
		entity.setCost(cost);
		entity.setLineNo(fDetail.getLineNo());
		entity.setLocCode(fDetail.getAssembleLoc());
		entity.setOrderNo(fDetail.getOrderNo());
		entity.setOrderType(WmsCodeMaster.ORDER_ASS.getCode());
		entity.setQtyOp(qtyOp);
		entity.setSkuCode(fDetail.getFittingSkuCode());
		return wmInventoryService.updateInventory(entity);
	}

	private WmAssembleHeader updateHeaderStatus(String orderNo) throws BusinessException {
		Map queryMap = new HashMap<>();
		queryMap.put("orderNo", orderNo);
		List<WmAssembleFDetailQueryItem> queryItems = this.getAllAssembleFDetailByOrderNo(queryMap);
		int sumOfFullAss = 0;
		int sumOfNew = 0;
		for (WmAssembleFDetailQueryItem item : queryItems) {
			if (item.getStatus().equals(WmsCodeMaster.ASS_FULL.getCode())) {
				sumOfFullAss++;
			} else if (item.getStatus().equals(WmsCodeMaster.ASS_NEW.getCode())) {
				sumOfNew++;
			}
		}
		List<WmAssembleHeader> headers = wmAssembleHeaderService.selectByKey(orderNo);
		if (headers.size() == 0) {
			throw new BusinessException("组装单[" + orderNo + "]数据不存在，请联系管理员");
		}
		WmAssembleHeader header = headers.get(0);
		if (sumOfFullAss == queryItems.size()) {
			header.setStatus(WmsCodeMaster.ASS_FULL.getCode());
		} else if (sumOfNew == queryItems.size()) {
			header.setStatus(WmsCodeMaster.ASS_CREATE_S.getCode());
		} else {
			header.setStatus(WmsCodeMaster.ASS_PART.getCode());
		}
		WmAssembleHeader savedHeader = wmAssembleHeaderService.saveAssembleOrder(header);
		return savedHeader;
	}

	private Message operateBeforeCheck(String orderNo) {
		Message message = new Message();
		List<WmAssembleHeader> headerList = wmAssembleHeaderService.selectByKey(orderNo);

		if (headerList.size() > 0) {
			WmAssembleHeader header = headerList.get(0);
			// 已审核或不审核
			if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(header.getAuditStatus())) {
				message.setCode(0);
				message.setMsg("组装单[" + orderNo + "]已审核,不能对明细进行编辑");
				return message;
			} else if (!WmsCodeMaster.INB_NEW.getCode().equals(header.getStatus())) {
				message.setCode(0);
				message.setMsg("组装单[" + orderNo + "]不是创建状态,不能对明细进行编辑");
				return message;
			} else {
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("组装单[" + orderNo + "]数据丢失,请联系管理员");
		return message;
	}
}
