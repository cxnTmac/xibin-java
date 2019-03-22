package com.xibin.wms.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.xibin.core.utils.CodeGenerator;
import com.xibin.core.utils.ComputeUtil;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmOutboundAllocMapper;
import com.xibin.wms.dao.WmOutboundDetailMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmOutboundAllocQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.WmInventoryService;
import com.xibin.wms.service.WmOutboundAllocService;
import com.xibin.wms.service.WmOutboundDetailService;
import com.xibin.wms.service.WmOutboundHeaderService;
import com.xibin.wms.service.WmOutboundUpdateService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmOutboundAllocServiceImpl extends BaseManagerImpl implements WmOutboundAllocService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmOutboundAllocMapper wmOutboundAllocMapper;
	@Resource
	private WmOutboundDetailMapper wmOutboundDetailMapper;
	@Autowired
	private WmOutboundDetailService wmOutboundDetailService;
	@Autowired
	private WmInventoryService wmInventoryService;
	@Autowired
	private WmOutboundHeaderService wmOutboundHeaderService;

	@Autowired
	private WmOutboundUpdateService wmOutboundUpdateService;

	@Override
	public WmOutboundAlloc getOutboundAllocById(int id) {
		// TODO Auto-generated method stub

		return wmOutboundAllocMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmOutboundAllocQueryItem> getAllOutboundAllocByPage(Map map) {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper.selectAllByPage(map);
	}

	@Override
	public List<WmOutboundAllocQueryItem> selectByKey(String orderNo, String lineNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return wmOutboundAllocMapper.selectByKey(orderNo, lineNo, userDetails.getCompanyId().toString(),
				userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper;
	}

	@Override
	public WmOutboundAlloc saveOutboundAlloc(WmOutboundAlloc model) {
		// TODO Auto-generated method stub
		// 只用于更新 成本
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		Integer num = 0;
		if (null == model.getId() || 0 == model.getId()) {
			model.setAllocId(CodeGenerator.getCodeByCurrentTimeAndRandomNum("ACT"));
		}
		return (WmOutboundAlloc) this.save(model);
	}

	private Message newOperateBeforeCheck(String orderNo) {
		Message message = new Message();
		List<WmOutboundHeaderQueryItem> headerList = wmOutboundHeaderService.selectByKey(orderNo);
		if (headerList.size() > 0) {
			WmOutboundHeaderQueryItem header = headerList.get(0);
			// 关闭定单不能编辑明细
			if (WmsCodeMaster.INB_CLOSE.getCode().equals(header.getStatus())) {
				message.setCode(0);
				message.setMsg("出库单[" + orderNo + "]不是创建状态,不能对明细进行编辑");
				return message;
			} else {
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("出库单[" + orderNo + "]数据丢失,请联系管理员");
		return message;
	}

	@Override
	public WmOutboundAlloc saveOutboundAllocForEditCost(WmOutboundAlloc model) throws BusinessException {
		// TODO Auto-generated method stub
		// 只用于更新 成本
		Message message = newOperateBeforeCheck(model.getOrderNo());
		if (message.getCode() == 0) {
			throw new BusinessException(message.getMsg());
		}
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		this.save(model);
		// 分配明细成本被修改后，更新出库明细的成本数据
		updateOutboundDetailForEditCost(model);
		return this.getOutboundAllocById(model.getId());
	}

	private void updateOutboundDetailForEditCost(WmOutboundAlloc model) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundDetail queryExample = new WmOutboundDetail();
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setWarehouseId(userDetails.getWarehouseId());
		queryExample.setOrderNo(model.getOrderNo());
		queryExample.setLineNo(model.getLineNo());
		List<WmOutboundDetail> queryResults = wmOutboundDetailService.selectByExample(queryExample);
		if (queryResults.size() > 0) {
			WmOutboundDetail detail = queryResults.get(0);
			WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
			allocQueryExample.setCompanyId(userDetails.getCompanyId());
			allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
			allocQueryExample.setOrderNo(model.getOrderNo());
			allocQueryExample.setLineNo(model.getLineNo());
			List<WmOutboundAlloc> allocQueryResults = this.selectByExample(allocQueryExample);
			double sumCost = 0.0;
			for (WmOutboundAlloc alloc : allocQueryResults) {
				sumCost = ComputeUtil.add(sumCost, ComputeUtil.mul(alloc.getCost(), alloc.getOutboundNum()));
			}
			double cost = ComputeUtil.div(sumCost, detail.getOutboundAllocNum(), 2);
			detail.setCost(cost);
			DaoUtil.save(detail, wmOutboundDetailMapper, session);
		} else {
			throw new BusinessException("订单号[" + model.getOrderNo() + "]行号[" + model.getLineNo() + "]的出库明细不存在，数据有误！");
		}
	}

	private int[] idListToArray(List<Integer> ids) {
		int[] idArray = new int[ids.size()];
		for (int i = 0; i < ids.size(); i++) {
			idArray[i] = ids.get(i);
		}
		return idArray;
	}

	@Override
	public List<WmOutboundAlloc> selectByExample(WmOutboundAlloc model) {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper.selectByExample(model);
	}

	@Override
	public Message shipByHeader(String orderNo) throws BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(userDetails.getCompanyId());
		headerQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundHeader> headers = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headers.size() > 0) {
			WmOutboundHeader header = headers.get(0);
			if (header.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_PART_ALLOC.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())) {
				throw new BusinessException("出库单[" + orderNo + "]没有拣货，不能发货");
			}
			WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
			allocQueryExample.setOrderNo(orderNo);
			allocQueryExample.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
			allocQueryExample.setCompanyId(userDetails.getCompanyId());
			allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
			List<WmOutboundAlloc> allocs = selectByExample(allocQueryExample);
			for (WmOutboundAlloc alloc : allocs) {
				try {
					shipByAlloc(alloc, false);
				} catch (BusinessException e) {
					// TODO: handle exception
					errors.add(e.getMessage());
				}
			}
			wmOutboundUpdateService.updateOutboundStatusByOrderNo(orderNo);
			if (errors.size() > 0) {
				message.setCode(100);
				message.setMsgs(errors);
				message.converMsgsToMsg("");
			} else {
				message.setCode(200);
				message.setMsg("操作成功");
			}
			return message;
		} else {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]不存在");
		}
	}

	@Override
	public Message cancelShipByHeader(String orderNo) throws BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(userDetails.getCompanyId());
		headerQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundHeader> headers = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headers.size() > 0) {
			WmOutboundHeader header = headers.get(0);
			if (header.getStatus().equals(WmsCodeMaster.SO_PART_SHIPPING.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
				WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
				allocQueryExample.setOrderNo(orderNo);
				allocQueryExample.setStatus(WmsCodeMaster.SO_FULL_SHIPPING.getCode());
				allocQueryExample.setCompanyId(userDetails.getCompanyId());
				allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
				List<WmOutboundAlloc> allocs = selectByExample(allocQueryExample);
				for (WmOutboundAlloc alloc : allocs) {
					try {
						cancelShipByAlloc(alloc, false);
					} catch (BusinessException e) {
						// TODO: handle exception
						errors.add(e.getMessage());
					}
				}
				wmOutboundUpdateService.updateOutboundStatusByOrderNo(orderNo);
				if (errors.size() > 0) {
					message.setCode(100);
					message.setMsgs(errors);
					message.converMsgsToMsg("");
				} else {
					message.setCode(200);
					message.setMsg("操作成功");
				}
				return message;

			} else {
				throw new BusinessException("出库单[" + orderNo + "]没有发货，不能取消发货");
			}
		} else {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]不存在");
		}
	}

	@Override
	public void shipByAlloc(WmOutboundAlloc alloc, boolean isUpdateOrder) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())
				&& !alloc.getStatus().equals(WmsCodeMaster.SO_OVER_PICKING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全拣货/超量状态，不能发货！");
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		entityFm.setActionCode(WmsCodeMaster.ACT_SHIP.getCode());
		entityFm.setLineNo(alloc.getLineNo());
		entityFm.setLocCode(alloc.getToLocCode());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityFm.setSkuCode(alloc.getSkuCode());
		entityFm.setQtyOp(alloc.getOutboundNum());
		entityFm.setPrice(alloc.getOutboundPrice());
		entityFm.setCost(alloc.getCost());
		// 更新库存
		wmInventoryService.updateInventory(entityFm);
		// 更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_SHIPPING.getCode());
		alloc.setShipOp(userDetails.getUserId());
		alloc.setShipTime(new Date());
		WmOutboundAlloc currentRecord = this.saveOutboundAlloc(alloc);
		if (isUpdateOrder) {
			wmOutboundUpdateService.updateOutboundStatusByAlloc(currentRecord);
		}

	}

	@Override
	public void cancelShipByAlloc(WmOutboundAlloc alloc, boolean isUpdateOrder) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全发货状态，不能取消发货！");
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_SHIP.getCode());
		entityFm.setLineNo(alloc.getLineNo());
		entityFm.setLocCode(alloc.getToLocCode());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityFm.setSkuCode(alloc.getSkuCode());
		entityFm.setQtyOp(alloc.getOutboundNum());
		entityFm.setPrice(alloc.getOutboundPrice());
		entityFm.setCost(alloc.getCost());
		// 更新库存
		wmInventoryService.updateInventory(entityFm);
		// 更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
		alloc.setShipOp(userDetails.getUserId());
		alloc.setShipTime(new Date());
		WmOutboundAlloc currentRecord = this.saveOutboundAlloc(alloc);
		if (isUpdateOrder) {
			wmOutboundUpdateService.updateOutboundStatusByAlloc(currentRecord);
		}
	}

	@Override
	public Message pickByOrderNo(String orderNo) throws BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(userDetails.getCompanyId());
		headerQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundHeader> headers = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headers.size() > 0) {
			WmOutboundHeader header = headers.get(0);
			if (header.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())
					|| header.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
				throw new BusinessException("数据有误，出库单[" + orderNo + "]为创建/完全拣货/完全发货状态,不能拣货");
			} else {
				WmOutboundAlloc queryExample = new WmOutboundAlloc();
				queryExample.setOrderNo(orderNo);
				queryExample.setCompanyId(userDetails.getCompanyId());
				queryExample.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
				List<WmOutboundAlloc> queryResults = this.selectByExample(queryExample);
				for (WmOutboundAlloc alloc : queryResults) {
					try {
						this.pickByAlloc(alloc, alloc.getPickNum());
					} catch (BusinessException e) {
						// TODO: handle exception
						errors.add(e.getMessage());
					}
				}
				// wmOutboundUpdateService.updateOutboundStatusByOrderNo(orderNo);
				if (errors.size() == 0) {
					message.setCode(200);
					message.setMsg("操作成功！");
				} else {
					message.setMsgs(errors);
					message.converMsgsToMsg("</br>");
					message.setCode(10);
				}
			}
		} else {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]不存在");
		}

		return message;
	}

	@Override
	public void pickByAlloc(WmOutboundAlloc alloc, double pickNum) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全分配状态，不能拣货！");
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		// 根据分配类别，分别设置不同的动作，普通拣货和预组装分配拣货
		if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(alloc.getAllocType())) {
			entityFm.setActionCode(WmsCodeMaster.ACT_PICK.getCode());
		} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(alloc.getAllocType())) {
			entityFm.setActionCode(WmsCodeMaster.ACT_PRE_ASSEMBLE_PICK.getCode());
		}
		entityFm.setLineNo(alloc.getLineNo());
		entityFm.setLocCode(alloc.getAllocLocCode());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityFm.setSkuCode(alloc.getSkuCode());
		entityFm.setQtyOp(alloc.getOutboundNum());
		entityFm.setCost(alloc.getCost());
		entityFm.setPrice(alloc.getOutboundPrice());
		InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
		// 根据分配类别，分别设置不同的动作，普通拣货和预组装分配拣货
		if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(alloc.getAllocType())) {
			entityTo.setActionCode(WmsCodeMaster.ACT_PICK.getCode());
		} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(alloc.getAllocType())) {
			entityTo.setActionCode(WmsCodeMaster.ACT_PRE_ASSEMBLE_PICK.getCode());
		}
		entityTo.setLineNo(alloc.getLineNo());
		entityTo.setLocCode(alloc.getToLocCode());
		entityTo.setOrderNo(alloc.getOrderNo());
		entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityTo.setSkuCode(alloc.getSkuCode());
		entityTo.setQtyOp(alloc.getOutboundNum());
		entityTo.setCost(alloc.getCost());
		entityTo.setPrice(alloc.getOutboundPrice());
		// 更新库存
		wmInventoryService.updateInventory(entityFm, entityTo);
		// 更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
		alloc.setPickNum(alloc.getOutboundNum());
		alloc.setPickOp(userDetails.getUserId());
		alloc.setPickTime(new Date());
		this.saveOutboundAlloc(alloc);
		// 更新明细的拣货数
		WmOutboundDetail queryExample = new WmOutboundDetail();
		queryExample.setOrderNo(alloc.getOrderNo());
		queryExample.setLineNo(alloc.getLineNo());
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundDetail> wmOutboundDetails = wmOutboundDetailService.selectByExample(queryExample);
		if (wmOutboundDetails.size() > 0) {

			WmOutboundDetail wmOutboundDetail = wmOutboundDetails.get(0);
			wmOutboundDetail
					.setOutboundPickNum(ComputeUtil.add(wmOutboundDetail.getOutboundPickNum(), alloc.getOutboundNum()));
			wmOutboundDetailService.saveOutboundDetailWithOutCheck(wmOutboundDetail);
		}
		// 如果是超量拣货
		if (alloc.getOutboundNum().doubleValue() < pickNum) {
			double overNum = ComputeUtil.sub(pickNum, alloc.getOutboundNum().doubleValue());

			// 创建商品明细
			WmOutboundDetail wmOutboundDetail = new WmOutboundDetail();
			wmOutboundDetail.setBuyerCode(alloc.getBuyerCode());
			wmOutboundDetail.setOrderNo(alloc.getOrderNo());
			wmOutboundDetail.setOutboundAllocNum(overNum);
			wmOutboundDetail.setOutboundNum(overNum);
			wmOutboundDetail.setOutboundPickNum(overNum);
			wmOutboundDetail.setOutboundPrice(alloc.getOutboundPrice());
			wmOutboundDetail.setPlanShipLoc(alloc.getToLocCode());
			wmOutboundDetail.setSkuCode(alloc.getSkuCode());
			wmOutboundDetail.setCost(alloc.getCost());
			wmOutboundDetail.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
			wmOutboundDetail.setWarehouseId(userDetails.getWarehouseId());
			wmOutboundDetail.setCompanyId(userDetails.getCompanyId());
			WmOutboundDetailQueryItem queryItem = wmOutboundDetailService
					.saveOutboundDetailWithOutCheck(wmOutboundDetail);
			// 创建分配明细
			WmOutboundAlloc wmOutboundAlloc = new WmOutboundAlloc();
			wmOutboundAlloc.setAllocLocCode(alloc.getAllocLocCode());
			wmOutboundAlloc.setBuyerCode(alloc.getBuyerCode());
			wmOutboundAlloc.setLineNo(queryItem.getLineNo());
			wmOutboundAlloc.setOrderNo(alloc.getOrderNo());
			wmOutboundAlloc.setOutboundNum(overNum);
			wmOutboundAlloc.setPickNum(overNum);
			wmOutboundAlloc.setOutboundPrice(alloc.getOutboundPrice());
			wmOutboundAlloc.setSkuCode(alloc.getSkuCode());
			wmOutboundAlloc.setToLocCode(alloc.getToLocCode());
			wmOutboundAlloc.setWarehouseId(userDetails.getWarehouseId());
			wmOutboundAlloc.setCompanyId(userDetails.getCompanyId());

			InventoryUpdateEntity entityFmForOverPick = new InventoryUpdateEntity();
			entityFmForOverPick.setActionCode(WmsCodeMaster.ACT_OVER_PICK.getCode());
			entityFmForOverPick.setLineNo(wmOutboundAlloc.getLineNo());
			entityFmForOverPick.setLocCode(wmOutboundAlloc.getAllocLocCode());
			entityFmForOverPick.setOrderNo(wmOutboundAlloc.getOrderNo());
			entityFmForOverPick.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityFmForOverPick.setSkuCode(wmOutboundAlloc.getSkuCode());
			entityFmForOverPick.setQtyOp(wmOutboundAlloc.getOutboundNum());
			entityFmForOverPick.setPrice(wmOutboundAlloc.getOutboundPrice());
			InventoryUpdateEntity entityToForOverPick = new InventoryUpdateEntity();
			entityToForOverPick.setActionCode(WmsCodeMaster.ACT_OVER_PICK.getCode());
			entityToForOverPick.setLineNo(wmOutboundAlloc.getLineNo());
			entityToForOverPick.setLocCode(wmOutboundAlloc.getToLocCode());
			entityToForOverPick.setOrderNo(wmOutboundAlloc.getOrderNo());
			entityToForOverPick.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityToForOverPick.setSkuCode(wmOutboundAlloc.getSkuCode());
			entityToForOverPick.setQtyOp(wmOutboundAlloc.getOutboundNum());
			entityToForOverPick.setPrice(wmOutboundAlloc.getOutboundPrice());
			// 更新库存
			WmActTran actTran = wmInventoryService.updateInventory(entityFmForOverPick, entityToForOverPick);
			wmOutboundAlloc.setCost(actTran.getCost());

			wmOutboundAlloc.setStatus(WmsCodeMaster.SO_OVER_PICKING.getCode());
			wmOutboundAlloc.setPickOp(userDetails.getUserId());
			wmOutboundAlloc.setPickTime(new Date());
			this.saveOutboundAlloc(wmOutboundAlloc);
		}
		wmOutboundUpdateService.updateOutboundStatusByOrderNo(alloc.getOrderNo());
	}

	@Override
	public void cancelPickByAlloc(WmOutboundAlloc alloc) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		if (!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())
				&& !alloc.getStatus().equals(WmsCodeMaster.SO_OVER_PICKING.getCode())) {
			throw new BusinessException(
					"出库单[" + alloc.getOrderNo() + "]分配明细[" + alloc.getAllocId() + "]不是完全拣货状态，不能取消拣货！");
		}
		if (alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())) {
			InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
			// 根据分配类别，分别设置不同的动作，普通拣货和预组装分配拣货
			if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(alloc.getAllocType())) {
				entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_PICK.getCode());
			} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(alloc.getAllocType())) {
				entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_PRE_ASSEMBLE_PICK.getCode());
			}

			entityFm.setLineNo(alloc.getLineNo());
			entityFm.setLocCode(alloc.getToLocCode());
			entityFm.setOrderNo(alloc.getOrderNo());
			entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityFm.setSkuCode(alloc.getSkuCode());
			entityFm.setQtyOp(alloc.getOutboundNum());
			entityFm.setCost(alloc.getCost());
			entityFm.setPrice(alloc.getOutboundPrice());
			InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
			// 根据分配类别，分别设置不同的动作，普通拣货和预组装分配拣货
			if (WmsCodeMaster.ALLOC_AUTO.getCode().equals(alloc.getAllocType())) {
				entityTo.setActionCode(WmsCodeMaster.ACT_CANCEL_PICK.getCode());
			} else if (WmsCodeMaster.ALLOC_ASS.getCode().equals(alloc.getAllocType())) {
				entityTo.setActionCode(WmsCodeMaster.ACT_CANCEL_PRE_ASSEMBLE_PICK.getCode());
			}
			entityTo.setLineNo(alloc.getLineNo());
			entityTo.setLocCode(alloc.getAllocLocCode());
			entityTo.setOrderNo(alloc.getOrderNo());
			entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityTo.setSkuCode(alloc.getSkuCode());
			entityTo.setQtyOp(alloc.getOutboundNum());
			entityTo.setCost(alloc.getCost());
			entityTo.setPrice(alloc.getOutboundPrice());
			// 更新库存
			wmInventoryService.updateInventory(entityFm, entityTo);
			// 更新分配明细状态
			alloc.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
			alloc.setPickOp(null);
			alloc.setPickTime(null);
			this.saveOutboundAlloc(alloc);
			// 更新明细的拣货数
			WmOutboundDetail queryExample = new WmOutboundDetail();
			queryExample.setOrderNo(alloc.getOrderNo());
			queryExample.setLineNo(alloc.getLineNo());
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setWarehouseId(userDetails.getWarehouseId());
			List<WmOutboundDetail> wmOutboundDetails = wmOutboundDetailService.selectByExample(queryExample);
			if (wmOutboundDetails.size() > 0) {
				WmOutboundDetail wmOutboundDetail = wmOutboundDetails.get(0);
				wmOutboundDetail.setOutboundPickNum(
						ComputeUtil.sub(wmOutboundDetail.getOutboundPickNum(), alloc.getOutboundNum()));
				wmOutboundDetailService.saveOutboundDetailWithOutCheck(wmOutboundDetail);
			}
		} else if (alloc.getStatus().equals(WmsCodeMaster.SO_OVER_PICKING.getCode())) {
			InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
			entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_OVER_PICK.getCode());
			entityFm.setLineNo(alloc.getLineNo());
			entityFm.setLocCode(alloc.getToLocCode());
			entityFm.setOrderNo(alloc.getOrderNo());
			entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityFm.setSkuCode(alloc.getSkuCode());
			entityFm.setQtyOp(alloc.getOutboundNum());
			entityFm.setCost(alloc.getCost());
			entityFm.setPrice(alloc.getOutboundPrice());
			InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
			entityTo.setActionCode(WmsCodeMaster.ACT_CANCEL_OVER_PICK.getCode());
			entityTo.setLineNo(alloc.getLineNo());
			entityTo.setLocCode(alloc.getAllocLocCode());
			entityTo.setOrderNo(alloc.getOrderNo());
			entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
			entityTo.setSkuCode(alloc.getSkuCode());
			entityTo.setQtyOp(alloc.getOutboundNum());
			entityTo.setCost(alloc.getCost());
			entityTo.setPrice(alloc.getOutboundPrice());
			// 更新库存
			wmInventoryService.updateInventory(entityFm, entityTo);
			WmOutboundDetail queryExample = new WmOutboundDetail();
			queryExample.setOrderNo(alloc.getOrderNo());
			queryExample.setLineNo(alloc.getLineNo());
			List<WmOutboundDetail> queryResults = wmOutboundDetailService.selectByExample(queryExample);
			if (queryResults.size() > 0) {
				DaoUtil.delete(queryResults.get(0).getId(), wmOutboundDetailMapper);
			}
			DaoUtil.delete(alloc.getId(), wmOutboundAllocMapper);
		}

		wmOutboundUpdateService.updateOutboundStatusByOrderNo(alloc.getOrderNo());
	}

}
