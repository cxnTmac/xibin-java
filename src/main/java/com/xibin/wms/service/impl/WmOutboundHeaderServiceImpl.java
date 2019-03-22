package com.xibin.wms.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmOutboundAllocMapper;
import com.xibin.wms.dao.WmOutboundDetailMapper;
import com.xibin.wms.dao.WmOutboundHeaderMapper;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.WmOutboundHeaderService;
import com.xibin.wms.service.WmToFinService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmOutboundHeaderServiceImpl extends BaseManagerImpl implements WmOutboundHeaderService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmOutboundHeaderMapper wmOutboundHeaderMapper;
	@Resource
	private WmOutboundDetailMapper wmOutboundDetailMapper;
	@Resource
	private WmOutboundAllocMapper wmOutboundAllocMapper;
	@Resource
	private WmToFinService wmToFinService;

	@Override
	public WmOutboundHeader getOutboundOrderById(int id) {
		// TODO Auto-generated method stub
		return wmOutboundHeaderMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmOutboundHeaderQueryItem> getAllOutboundOrderByPage(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundHeaderMapper.selectAllByPage(map);
	}

	@Override
	public List<WmOutboundHeaderQueryItem> selectByKey(String orderNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return wmOutboundHeaderMapper.selectByKey(orderNo, userDetails.getCompanyId().toString(),
				userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmOutboundHeaderMapper;
	}

	@Override
	public WmOutboundHeaderQueryItem saveOutbound(WmOutboundHeader model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if (null == model.getId() || model.getId() == 0) {
			model.setOrderNo(CodeGenerator.getCodeByCurrentTimeAndRandomNum("OUB"));
		}
		this.save(model);
		List<WmOutboundHeaderQueryItem> list = selectByKey(model.getOrderNo());
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public int remove(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if (queryResult.size() > 0) {
			WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
			if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmOutboundHeader.getAuditStatus())) {
				throw new BusinessException("入库单[" + wmOutboundHeader.getOrderNo() + "]已审核");
			} else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())) {
				throw new BusinessException("入库单[" + wmOutboundHeader.getOrderNo() + "]不是创建状态");
			}
			return this.delete(wmOutboundHeader.getId());
		} else {
			throw new BusinessException("入库单[" + orderNo + "]不存在");
		}
	}

	@Override
	public List<WmOutboundHeader> selectByExample(WmOutboundHeader model) {
		// TODO Auto-generated method stub
		return wmOutboundHeaderMapper.selectByExample(model);
	}

	@Override
	public WmOutboundHeaderQueryItem audit(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if (queryResult.size() > 0) {
			WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
			if (WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmOutboundHeader.getAuditStatus())) {
				throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]已审核");
			} else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())) {
				throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是创建状态");
			}
			wmOutboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_CLOSE.getCode());
			wmOutboundHeader.setAuditOp(userDetails.getUserId());
			wmOutboundHeader.setAuditTime(new Date());
			return this.saveOutbound(wmOutboundHeader);
		} else {
			throw new BusinessException("出库单[" + orderNo + "]不存在");
		}
	}

	@Override
	public WmOutboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if (queryResult.size() > 0) {
			WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
			if (WmsCodeMaster.AUDIT_NEW.getCode().equals(wmOutboundHeader.getAuditStatus())) {
				throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]未审核");
			} else if (!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())) {
				throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是创建状态");
			}
			wmOutboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_NEW.getCode());
			wmOutboundHeader.setAuditOp(null);
			wmOutboundHeader.setAuditTime(null);
			return this.saveOutbound(wmOutboundHeader);
		} else {
			throw new BusinessException("出库单[" + orderNo + "]不存在");
		}
	}

	@Override
	public WmOutboundHeaderQueryItem close(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if (queryResult.size() > 0) {
			WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
			if (!WmsCodeMaster.SO_FULL_SHIPPING.getCode().equals(wmOutboundHeader.getStatus())) {
				throw new BusinessException("出库单[" + wmOutboundHeader.getOrderNo() + "]不是完全发货状态");
			}
			wmOutboundHeader.setStatus(WmsCodeMaster.INB_CLOSE.getCode());
			wmOutboundHeader.setAuditOp(userDetails.getUserId());
			wmOutboundHeader.setAuditTime(new Date());
			return this.saveOutbound(wmOutboundHeader);
		} else {
			throw new BusinessException("出库单[" + orderNo + "]不存在");
		}
	}

	@Override
	public Message accountByOrderNo(String orderNo) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmOutboundHeaderQueryItem> results = this.selectByKey(orderNo);
		if (results.isEmpty()) {
			message.setCode(0);
			message.setMsg("出库单[" + orderNo + "]不存在");
			return message;
		} else if (results.get(0).getIsCalculated().equals("Y")) {
			message.setCode(0);
			message.setMsg("出库单[" + orderNo + "]已经生成了销售凭证");
			return message;
		}
		WmOutboundHeaderQueryItem headerQueryItem = results.get(0);
		Map map = new HashMap<>();
		map.put("companyId", userDetails.getCompanyId());
		map.put("warehouseId", userDetails.getWarehouseId());
		List<String> orderNos = new ArrayList<String>();
		orderNos.add(headerQueryItem.getOrderNo());
		map.put("orderNos", orderNos);
		map.put("outboundType", headerQueryItem.getOutboundType());
		List<WmOutboundDetailSumPriceQueryItem> sumResults = wmOutboundDetailMapper
				.querySumPriceGroupByBuyerForAccount(map);
		message = wmToFinService.accountOutboundDetails(sumResults, headerQueryItem.getOutboundType());

		FiVoucher voucher = (FiVoucher) message.getData();
		if (message.getCode() == 200) {
			message.setMsg("生成了凭证：" + voucher.getVoucherWord() + voucher.getVoucherNum());
		}
		// 更新所有订单状态
		Map updateMap = new HashMap<>();
		updateMap.put("companyId", userDetails.getCompanyId());
		updateMap.put("warehouseId", userDetails.getWarehouseId());
		updateMap.put("orderNos", orderNos);
		updateMap.put("outboundType", headerQueryItem.getOutboundType());
		updateMap.put("toVoucherId", voucher.getId());
		updateMap.put("isCalculated", "Y");
		wmOutboundHeaderMapper.updateCalculateByOrderNos(updateMap);

		return message;
	}

	@Override
	public Message accountByOrderNos(List<String> orderNos, String outboundType) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map map = new HashMap<>();
		map.put("companyId", userDetails.getCompanyId());
		map.put("warehouseId", userDetails.getWarehouseId());
		map.put("orderNos", orderNos);
		map.put("status", "99");
		map.put("isCalculated", "N");
		List<String> resultOrders = wmOutboundHeaderMapper.queryOrderNosByStatus(map);
		orderNos.removeAll(resultOrders);
		List<String> errors = new ArrayList<String>();
		for (String orderNo : orderNos) {
			errors.add("订单：[" + orderNo + "]不存在或者不是关闭状态，不能核算生成凭证!");
		}
		if (errors.size() > 0) {
			message.setCode(100);
		}
		if (resultOrders.size() == 0) {
			message.setCode(0);
			return message;
		}
		map.put("orderNos", resultOrders);
		map.put("outboundType", outboundType);
		List<WmOutboundDetailSumPriceQueryItem> sumResults = wmOutboundDetailMapper
				.querySumPriceGroupByBuyerForAccount(map);
		message = wmToFinService.accountOutboundDetails(sumResults, outboundType);
		FiVoucher voucher = (FiVoucher) message.getData();
		// 更新所有订单状态
		Map updateMap = new HashMap<>();
		updateMap.put("companyId", userDetails.getCompanyId());
		updateMap.put("warehouseId", userDetails.getWarehouseId());
		updateMap.put("orderNos", resultOrders);
		updateMap.put("outboundType", outboundType);
		updateMap.put("toVoucherId", voucher.getId());
		updateMap.put("isCalculated", "Y");
		wmOutboundHeaderMapper.updateCalculateByOrderNos(updateMap);
		return message;
	}

	@Override
	public Message accountForCostByOrderNo(String orderNo) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmOutboundHeaderQueryItem> results = this.selectByKey(orderNo);
		if (results.isEmpty()) {
			message.setCode(0);
			message.setMsg("入库单[" + orderNo + "]不存在");
			return message;
		} else if (results.get(0).getIsCostCalculated().equals("Y")) {
			message.setCode(0);
			message.setMsg("入库单[" + orderNo + "]已经生成了成本凭证！");
			return message;
		}
		WmOutboundHeaderQueryItem headerQueryItem = results.get(0);
		Map map = new HashMap<>();
		map.put("companyId", userDetails.getCompanyId());
		map.put("warehouseId", userDetails.getWarehouseId());
		List<String> orderNos = new ArrayList<String>();
		orderNos.add(headerQueryItem.getOrderNo());
		map.put("orderNos", orderNos);
		// map.put("outboundType", headerQueryItem.getOutboundType());
		Double sumResults = wmOutboundAllocMapper.querySumCostForAccount(map);
		// 对金额取两位小数
		BigDecimal bg = new BigDecimal(sumResults);
		sumResults = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		message = wmToFinService.accountOutboundCost(sumResults, headerQueryItem.getOutboundType());

		FiVoucher voucher = (FiVoucher) message.getData();
		if (message.getCode() == 200) {
			message.setMsg("生成了凭证：" + voucher.getVoucherWord() + voucher.getVoucherNum());
		}
		// 更新所有订单状态
		Map updateMap = new HashMap<>();
		updateMap.put("companyId", userDetails.getCompanyId());
		updateMap.put("warehouseId", userDetails.getWarehouseId());
		updateMap.put("orderNos", orderNos);
		// updateMap.put("outboundType", headerQueryItem.getOutboundType());
		updateMap.put("toCostVoucherId", voucher.getId());
		updateMap.put("isCostCalculated", "Y");
		wmOutboundHeaderMapper.updateCostCalculateByOrderNos(updateMap);
		return message;
	}

	@Override
	public Message accountForCostByOrderNos(List<String> orderNos, String outboundType) throws BusinessException {
		Message message = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map map = new HashMap<>();
		map.put("companyId", userDetails.getCompanyId());
		map.put("warehouseId", userDetails.getWarehouseId());
		map.put("orderNos", orderNos);
		map.put("status", "99");
		map.put("isCostCalculated", "N");
		List<String> resultOrders = wmOutboundHeaderMapper.queryOrderNosByStatus(map);
		orderNos.removeAll(resultOrders);
		List<String> errors = new ArrayList<String>();
		for (String orderNo : orderNos) {
			errors.add("订单：[" + orderNo + "]不存在或者不是完成销售核算状态，不能核算生成成本凭证!");
		}
		if (errors.size() > 0) {
			message.setCode(100);
		}
		if (resultOrders.size() == 0) {
			message.setCode(0);
			return message;
		}
		map.put("orderNos", resultOrders);
		Double sumResults = wmOutboundAllocMapper.querySumCostForAccount(map);
		// 对金额取两位小数
		BigDecimal bg = new BigDecimal(sumResults);
		sumResults = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		message = wmToFinService.accountOutboundCost(sumResults, outboundType);
		FiVoucher voucher = (FiVoucher) message.getData();
		// 更新所有订单状态
		Map updateMap = new HashMap<>();
		updateMap.put("companyId", userDetails.getCompanyId());
		updateMap.put("warehouseId", userDetails.getWarehouseId());
		updateMap.put("orderNos", resultOrders);
		updateMap.put("toCostVoucherId", voucher.getId());
		updateMap.put("isCostCalculated", "Y");
		wmOutboundHeaderMapper.updateCostCalculateByOrderNos(updateMap);
		return message;
	}

}
