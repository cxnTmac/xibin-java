package com.xibin.wms.service.impl;

import java.util.Date;
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
import com.xibin.core.utils.ComputeUtil;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInventoryMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.query.WmInventoryQueryItem;
import com.xibin.wms.service.WmActTranService;
import com.xibin.wms.service.WmInventoryService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmInventoryServiceImpl extends BaseManagerImpl implements WmInventoryService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmInventoryMapper wmInventoryMapper;
	@Resource
	private WmActTranService wmActTranService;

	@Override
	public WmInventory getInventoryById(int id) {
		// TODO Auto-generated method stub
		return wmInventoryMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmInventoryQueryItem> getAllInventoryByPage(Map map) {
		// TODO Auto-generated method stub
		return wmInventoryMapper.selectAllByPage(map);
	}

	@Override
	public List<WmInventoryQueryItem> selectByKey(String skuCode, String locCode, String lot) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return wmInventoryMapper.selectByKey(skuCode, locCode, lot, userDetails.getCompanyId().toString(),
				userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmInventoryMapper;
	}

	@Override
	public WmActTran updateInventory(InventoryUpdateEntity fmIn) throws BusinessException {
		// TODO Auto-generated method stub
		String actionCode = fmIn.getActionCode();
		WmActTran actTran = new WmActTran();
		String checkResult = checkBeforeReceive(fmIn);
		if (checkResult != null) {
			throw new BusinessException(checkResult);
		}
		if (WmsCodeMaster.ACT_REC.getCode().equals(actionCode)
				|| WmsCodeMaster.ACT_RE_REC.getCode().equals(actionCode)) {
			// 收货
			actTran = receiveOperation(fmIn);
		} else if (WmsCodeMaster.ACT_CANCEL_REC.getCode().equals(actionCode)
				|| WmsCodeMaster.ACT_RE_CANCEL_REC.getCode().equals(actionCode)) {
			// 取消收货
			actTran = cancelReceiveOperation(fmIn);
		} else if (WmsCodeMaster.ACT_ALLOC.getCode().equals(actionCode)) {
			actTran = allocOperation(fmIn);
		} else if (WmsCodeMaster.ACT_CANCEL_ALLOC.getCode().equals(actionCode)) {
			actTran = cancelAllocOperation(fmIn);
		} else if (WmsCodeMaster.ACT_CANCEL_SHIP.getCode().equals(actionCode)) {
			actTran = cancelShipOperation(fmIn);
		} else if (WmsCodeMaster.ACT_SHIP.getCode().equals(actionCode)) {
			actTran = shipOperation(fmIn);
		} else if (WmsCodeMaster.ACT_ASSEMBLE_S.getCode().equals(actionCode)) {
			// 组装消耗子件
			actTran = assembleSOperation(fmIn);
		} else if (WmsCodeMaster.ACT_ASSEMBLE_F.getCode().equals(actionCode)) {
			// 组装生成父件
			actTran = assembleFOperation(fmIn);
		} else if (WmsCodeMaster.ACT_PRE_ASSEMBLE_ALLOC.getCode().equals(actionCode)) {
			// 预组装分配
			actTran = this.preAssembleAllocOperation(fmIn);
		} else if (WmsCodeMaster.ACT_CANCEL_PRE_ASSEMBLE_ALLOC.getCode().equals(actionCode)) {
			// 预组装分配
			actTran = this.cancelPreAssembleAllocOperation(fmIn);
		} else {
			throw new BusinessException("操作码[" + actionCode + "]有误！");
		}
		// 写入交易记录
		return addActTran(actTran);
	}

	@Override
	public WmActTran updateInventory(InventoryUpdateEntity fmIn, InventoryUpdateEntity toIn) throws BusinessException {
		// TODO Auto-generated method stub
		String actionCode = fmIn.getActionCode();
		WmActTran actTran = new WmActTran();
		String checkResult = checkBeforeReceive(fmIn);
		if (checkResult != null) {
			throw new BusinessException(checkResult);
		}
		if (WmsCodeMaster.ACT_PICK.getCode().equals(actionCode)
				|| WmsCodeMaster.ACT_PRE_ASSEMBLE_PICK.getCode().equals(actionCode)) {
			// 拣货
			actTran = pickOperation(fmIn, toIn);
		} else if (WmsCodeMaster.ACT_CANCEL_PICK.getCode().equals(actionCode)
				|| WmsCodeMaster.ACT_CANCEL_PRE_ASSEMBLE_PICK.getCode().equals(actionCode)) {
			actTran = cancelPickOperation(fmIn, toIn);
		} else if (WmsCodeMaster.ACT_OVER_PICK.getCode().equals(actionCode)) {
			actTran = overPickOperation(fmIn, toIn);
		} else if (WmsCodeMaster.ACT_CANCEL_OVER_PICK.getCode().equals(actionCode)) {
			actTran = cancelOverPickOperation(fmIn, toIn);
		} else if (WmsCodeMaster.ACT_MOVE.getCode().equals(actionCode)) {
			actTran = moveOperation(fmIn, toIn);
		} else if (WmsCodeMaster.ACT_TRANSFER.getCode().equals(actionCode)) {
			actTran = transferOperation(fmIn, toIn);
		} else {
			throw new BusinessException("操作码[" + actionCode + "]有误！");
		}
		// 写入交易记录
		return addActTran(actTran);
	}

	private String checkBeforeReceive(InventoryUpdateEntity entity) {
		if (null == entity.getActionCode() || entity.getActionCode().isEmpty()) {
			return "库存更新操作码为空！";
		} else if (null == entity.getSkuCode() || entity.getSkuCode().isEmpty()) {
			return "库存更新产品编码[" + entity.getActionCode() + "]为空！";
		} else if (null == entity.getLocCode() || entity.getLocCode().isEmpty()) {
			return "库存更新目标库位编码[" + entity.getActionCode() + "]为空！";
		}
		return null;
	}

	private WmActTran shipOperation(InventoryUpdateEntity fmIn) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setPrice(fmIn.getPrice());
		actTran.setCost(fmIn.getCost());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			if (fmInventory.getInvAvailableNum().doubleValue() < fmIn.getQtyOp().doubleValue()) {
				throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]可用数不足！");
			} else {
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
				fmInventory.setInvNum(ComputeUtil.sub(fmInventory.getInvNum(), fmIn.getQtyOp()));
				Double oldAvailableNum = fmInventory.getInvAvailableNum();
				fmInventory.setInvAvailableNum(ComputeUtil.sub(oldAvailableNum, fmIn.getQtyOp()));

				// 按照计算的成本价格减少库存价值
				fmInventory.setTotalPrice(
						ComputeUtil.sub(fmInventory.getTotalPrice(), ComputeUtil.mul(fmIn.getQtyOp(), fmIn.getCost())));
				this.save(fmInventory);
			}
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		return actTran;
	}

	private WmActTran cancelShipOperation(InventoryUpdateEntity toIn) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(toIn.getLineNo());
		actTran.setOrderNo(toIn.getOrderNo());
		actTran.setOrderType(toIn.getOrderType());
		actTran.setTranType(toIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setCost(toIn.getCost());
		actTran.setPrice(toIn.getPrice());
		actTran.setToSku(toIn.getSkuCode());
		actTran.setToLot(toIn.getLotNum());
		actTran.setToLoc(toIn.getLocCode());
		actTran.setToQtyOp(toIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(toIn.getSkuCode());
		fmQueryExample.setLocCode(toIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			actTran.setToQtyBefore(fmInventory.getInvAvailableNum());
			actTran.setToQtyAfter(ComputeUtil.add(fmInventory.getInvAvailableNum(), toIn.getQtyOp()));
			fmInventory.setInvNum(ComputeUtil.add(fmInventory.getInvNum(), toIn.getQtyOp()));
			fmInventory.setInvAvailableNum(ComputeUtil.add(fmInventory.getInvAvailableNum(), toIn.getQtyOp()));
			// 增加库存价值
			fmInventory.setTotalPrice(
					ComputeUtil.add(fmInventory.getTotalPrice(), ComputeUtil.mul(toIn.getQtyOp(), toIn.getCost())));
			this.save(fmInventory);
		} else {
			WmInventory toInventory = new WmInventory();
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyAfter(toIn.getQtyOp());
			toInventory.setAllocNum(0.0);
			toInventory.setCompanyId(userDetails.getCompanyId());
			toInventory.setWarehouseId(userDetails.getWarehouseId());
			toInventory.setInvAvailableNum(toIn.getQtyOp());
			toInventory.setInvNum(toIn.getQtyOp());
			toInventory.setLocCode(toIn.getLocCode());
			toInventory.setSkuCode(toIn.getSkuCode());
			// 增加库存价值
			toInventory.setTotalPrice(ComputeUtil.mul(toIn.getQtyOp(), toIn.getCost()));
			this.save(toInventory);
		}
		return actTran;
	}

	// 超量拣货
	private WmActTran overPickOperation(InventoryUpdateEntity fmIn, InventoryUpdateEntity toIn)
			throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setPrice(fmIn.getPrice());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setToSku(toIn.getSkuCode());
		actTran.setToLot(toIn.getLotNum());
		actTran.setToLoc(toIn.getLocCode());
		actTran.setToQtyOp(toIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		double cost = 0.0;
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			if (fmInventory.getInvAvailableNum().doubleValue() < fmIn.getQtyOp().doubleValue()) {
				throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]可用数不足！");
			} else {
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp().doubleValue()));
				// 计算成本
				cost = ComputeUtil.div(fmInventory.getTotalPrice(), fmInventory.getInvAvailableNum(), 2);
				actTran.setCost(cost);
				fmInventory.setInvNum(ComputeUtil.sub(fmInventory.getInvNum().doubleValue(), fmIn.getQtyOp()));
				fmInventory.setInvAvailableNum(
						ComputeUtil.sub(fmInventory.getInvAvailableNum().doubleValue(), fmIn.getQtyOp()));
				fmInventory.setTotalPrice(ComputeUtil.sub(fmInventory.getTotalPrice().doubleValue(),
						ComputeUtil.mul(cost, fmIn.getQtyOp())));
				this.save(fmInventory);
			}
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		WmInventory toQueryExample = new WmInventory();
		toQueryExample.setSkuCode(toIn.getSkuCode());
		toQueryExample.setLocCode(toIn.getLocCode());
		toQueryExample.setCompanyId(userDetails.getCompanyId());
		toQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> toInventoryList = selectByExample(toQueryExample);
		if (toInventoryList.size() > 0) {
			WmInventory toInventory = toInventoryList.get(0);
			actTran.setToQtyBefore(toInventory.getInvAvailableNum());
			actTran.setToQtyAfter(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));
			toInventory.setInvNum(ComputeUtil.add(toInventory.getInvNum(), toIn.getQtyOp()));
			toInventory.setInvAvailableNum(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));

			// 库存价值增加
			toInventory.setTotalPrice(
					ComputeUtil.add(toInventory.getTotalPrice(), ComputeUtil.mul(toIn.getQtyOp(), cost)));
			this.save(toInventory);
		} else {
			WmInventory toInventory = new WmInventory();
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyAfter(toIn.getQtyOp());
			toInventory.setAllocNum(0.0);
			toInventory.setCompanyId(userDetails.getCompanyId());
			toInventory.setWarehouseId(userDetails.getWarehouseId());
			toInventory.setInvAvailableNum(toIn.getQtyOp());
			toInventory.setInvNum(toIn.getQtyOp());
			toInventory.setLocCode(toIn.getLocCode());
			toInventory.setSkuCode(toIn.getSkuCode());
			// 库存价值增加
			toInventory.setTotalPrice(ComputeUtil.mul(toIn.getQtyOp(), fmIn.getCost()));
			this.save(toInventory);
		}
		return actTran;
	}

	private WmActTran pickOperation(InventoryUpdateEntity fmIn, InventoryUpdateEntity toIn) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setCost(fmIn.getCost());
		actTran.setPrice(fmIn.getPrice());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setToSku(toIn.getSkuCode());
		actTran.setToLot(toIn.getLotNum());
		actTran.setToLoc(toIn.getLocCode());
		actTran.setToQtyOp(toIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			if (fmInventory.getAllocNum().doubleValue() < fmIn.getQtyOp().doubleValue()) {
				throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]已分配数不足！");
			} else {
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(fmInventory.getInvAvailableNum());
				// 普通分配的拣货需要扣除库存数，预组装分配的拣货不需要扣除库存数，因为预组装数是虚拟的，不计算入库存数，库存数=分配数+可用数-预组装数
				if (WmsCodeMaster.ACT_PICK.getCode().equals(fmIn.getActionCode())) {
					fmInventory.setInvNum(ComputeUtil.sub(fmInventory.getInvNum(), fmIn.getQtyOp()));
				}
				fmInventory.setAllocNum(ComputeUtil.sub(fmInventory.getAllocNum(), fmIn.getQtyOp()));
				this.save(fmInventory);
			}
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		WmInventory toQueryExample = new WmInventory();
		toQueryExample.setSkuCode(toIn.getSkuCode());
		toQueryExample.setLocCode(toIn.getLocCode());
		toQueryExample.setCompanyId(userDetails.getCompanyId());
		toQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> toInventoryList = selectByExample(toQueryExample);
		if (toInventoryList.size() > 0) {
			WmInventory toInventory = toInventoryList.get(0);
			actTran.setToQtyBefore(toInventory.getInvAvailableNum());
			actTran.setToQtyAfter(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));
			toInventory.setInvNum(ComputeUtil.add(toInventory.getInvNum(), toIn.getQtyOp()));
			toInventory.setInvAvailableNum(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));

			// 库存价值增加
			toInventory.setTotalPrice(
					ComputeUtil.add(toInventory.getTotalPrice(), ComputeUtil.mul(toIn.getQtyOp(), fmIn.getCost())));
			this.save(toInventory);
		} else {
			WmInventory toInventory = new WmInventory();
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyAfter(toIn.getQtyOp());
			toInventory.setAllocNum(0.0);
			toInventory.setCompanyId(userDetails.getCompanyId());
			toInventory.setWarehouseId(userDetails.getWarehouseId());
			toInventory.setInvAvailableNum(toIn.getQtyOp());
			toInventory.setInvNum(toIn.getQtyOp());
			toInventory.setLocCode(toIn.getLocCode());
			toInventory.setSkuCode(toIn.getSkuCode());
			// 库存价值增加
			toInventory.setTotalPrice(ComputeUtil.mul(toIn.getQtyOp(), fmIn.getCost()));
			this.save(toInventory);
		}
		return actTran;
	}

	private WmActTran cancelPickOperation(InventoryUpdateEntity fmIn, InventoryUpdateEntity toIn)
			throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setToSku(toIn.getSkuCode());
		actTran.setToLot(toIn.getLotNum());
		actTran.setToLoc(toIn.getLocCode());
		actTran.setToQtyOp(toIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			if (fmInventory.getInvAvailableNum().doubleValue() < fmIn.getQtyOp().doubleValue()) {
				throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]可用数不足！");
			} else {
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
				fmInventory.setInvNum(fmInventory.getInvNum() - fmIn.getQtyOp());
				fmInventory.setInvAvailableNum(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
				// 减少库存价值
				fmInventory.setTotalPrice(
						ComputeUtil.sub(fmInventory.getTotalPrice(), ComputeUtil.mul(fmIn.getQtyOp(), fmIn.getCost())));
				this.save(fmInventory);
			}
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		WmInventory toQueryExample = new WmInventory();
		toQueryExample.setSkuCode(toIn.getSkuCode());
		toQueryExample.setLocCode(toIn.getLocCode());
		toQueryExample.setCompanyId(userDetails.getCompanyId());
		toQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> toInventoryList = selectByExample(toQueryExample);
		if (toInventoryList.size() > 0) {
			WmInventory toInventory = toInventoryList.get(0);
			actTran.setToQtyBefore(toInventory.getInvAvailableNum());
			actTran.setToQtyAfter(toInventory.getInvAvailableNum());
			// 普通分配的拣货需要加入库存数，预组装分配的拣货不需要加入库存数，因为预组装数是虚拟的，不计算入库存数，库存数=分配数+可用数-预组装数
			if (WmsCodeMaster.ACT_PICK.getCode().equals(fmIn.getActionCode())) {
				toInventory.setInvNum(ComputeUtil.add(toInventory.getInvNum(), toIn.getQtyOp()));
			}
			toInventory.setAllocNum(ComputeUtil.add(toInventory.getAllocNum(), toIn.getQtyOp()));
			this.save(toInventory);
		} else {
			WmInventory toInventory = new WmInventory();
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyAfter(0.0);
			toInventory.setAllocNum(toIn.getQtyOp());
			toInventory.setCompanyId(userDetails.getCompanyId());
			toInventory.setWarehouseId(userDetails.getWarehouseId());
			toInventory.setInvAvailableNum(0.0);
			if (WmsCodeMaster.ACT_CANCEL_PICK.getCode().equals(fmIn.getActionCode())) {
				toInventory.setInvNum(toIn.getQtyOp());
			} else if (WmsCodeMaster.ACT_CANCEL_PRE_ASSEMBLE_PICK.getCode().equals(fmIn.getActionCode())) {
				toInventory.setInvNum(0.0);
			}
			toInventory.setPreAssembleNum(toIn.getQtyOp());
			toInventory.setLocCode(toIn.getLocCode());
			toInventory.setSkuCode(toIn.getSkuCode());
			this.save(toInventory);
		}
		return actTran;
	}

	private WmActTran cancelOverPickOperation(InventoryUpdateEntity fmIn, InventoryUpdateEntity toIn)
			throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setToSku(toIn.getSkuCode());
		actTran.setToLot(toIn.getLotNum());
		actTran.setToLoc(toIn.getLocCode());
		actTran.setToQtyOp(toIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			if (fmInventory.getInvAvailableNum().doubleValue() < fmIn.getQtyOp().doubleValue()) {
				throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]可用数不足！");
			} else {
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
				fmInventory.setInvNum(ComputeUtil.sub(fmInventory.getInvNum(), fmIn.getQtyOp()));
				fmInventory.setInvAvailableNum(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
				// 减少库存价值
				fmInventory.setTotalPrice(
						ComputeUtil.sub(fmInventory.getTotalPrice(), ComputeUtil.mul(fmIn.getQtyOp(), fmIn.getCost())));
				this.save(fmInventory);
			}
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		WmInventory toQueryExample = new WmInventory();
		toQueryExample.setSkuCode(toIn.getSkuCode());
		toQueryExample.setLocCode(toIn.getLocCode());
		toQueryExample.setCompanyId(userDetails.getCompanyId());
		toQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> toInventoryList = selectByExample(toQueryExample);
		if (toInventoryList.size() > 0) {
			WmInventory toInventory = toInventoryList.get(0);
			actTran.setToQtyBefore(toInventory.getInvAvailableNum());
			actTran.setToQtyAfter(toInventory.getInvAvailableNum());
			toInventory.setInvNum(ComputeUtil.add(toInventory.getInvNum(), toIn.getQtyOp()));
			toInventory.setInvAvailableNum(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));
			this.save(toInventory);
		} else {
			WmInventory toInventory = new WmInventory();
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyAfter(0.0);
			toInventory.setAllocNum(0.0);
			toInventory.setCompanyId(userDetails.getCompanyId());
			toInventory.setWarehouseId(userDetails.getWarehouseId());
			toInventory.setInvAvailableNum(toIn.getQtyOp());
			toInventory.setInvNum(toIn.getQtyOp());
			toInventory.setLocCode(toIn.getLocCode());
			toInventory.setSkuCode(toIn.getSkuCode());
			this.save(toInventory);
		}
		return actTran;
	}

	private WmActTran moveOperation(InventoryUpdateEntity fmIn, InventoryUpdateEntity toIn) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setToSku(toIn.getSkuCode());
		actTran.setToLot(toIn.getLotNum());
		actTran.setToLoc(toIn.getLocCode());
		actTran.setToQtyOp(toIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		double avg = 0.0;
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			if (fmInventory.getInvAvailableNum().doubleValue() < fmIn.getQtyOp().doubleValue()) {
				throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]可用数小于移动数！");
			}
			actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
			actTran.setFmQtyAfter(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
			// 分配时就计算成本
			avg = ComputeUtil.div(fmInventory.getTotalPrice(), fmInventory.getInvAvailableNum(), 2);
			fmInventory.setInvNum(ComputeUtil.sub(fmInventory.getInvNum(), fmIn.getQtyOp()));
			fmInventory.setInvAvailableNum(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));

			// 减少库存价值
			fmInventory
					.setTotalPrice(ComputeUtil.sub(fmInventory.getTotalPrice(), ComputeUtil.mul(fmIn.getQtyOp(), avg)));
			this.save(fmInventory);
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		WmInventory toQueryExample = new WmInventory();
		toQueryExample.setSkuCode(toIn.getSkuCode());
		toQueryExample.setLocCode(toIn.getLocCode());
		toQueryExample.setCompanyId(userDetails.getCompanyId());
		toQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> toInventoryList = selectByExample(toQueryExample);
		if (toInventoryList.size() > 0) {
			WmInventory toInventory = toInventoryList.get(0);
			actTran.setToQtyBefore(toInventory.getInvAvailableNum());
			actTran.setToQtyAfter(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));
			toInventory.setInvNum(ComputeUtil.add(toInventory.getInvNum(), toIn.getQtyOp()));
			toInventory.setInvAvailableNum(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));
			// 增加库存价值
			toInventory.setTotalPrice(
					ComputeUtil.add(toInventory.getTotalPrice(), ComputeUtil.mul(toIn.getQtyOp().doubleValue(), avg)));
			this.save(toInventory);
		} else {
			WmInventory toInventory = new WmInventory();
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyAfter(0.0);
			toInventory.setAllocNum(0.0);
			toInventory.setCompanyId(userDetails.getCompanyId());
			toInventory.setWarehouseId(userDetails.getWarehouseId());
			toInventory.setInvAvailableNum(toIn.getQtyOp());
			toInventory.setInvNum(toIn.getQtyOp());
			toInventory.setLocCode(toIn.getLocCode());
			toInventory.setSkuCode(toIn.getSkuCode());
			toInventory.setTotalPrice(ComputeUtil.mul(toIn.getQtyOp().doubleValue(), avg));
			this.save(toInventory);
		}
		return actTran;
	}

	private WmActTran transferOperation(InventoryUpdateEntity fmIn, InventoryUpdateEntity toIn)
			throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setToSku(toIn.getSkuCode());
		actTran.setToLot(toIn.getLotNum());
		actTran.setToLoc(toIn.getLocCode());
		actTran.setToQtyOp(toIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		double avg = 0.0;
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			if (fmInventory.getInvAvailableNum().doubleValue() < fmIn.getQtyOp().doubleValue()) {
				throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]可用数小于移动数！");
			}
			actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
			actTran.setFmQtyAfter(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
			// 分配时就计算成本
			avg = ComputeUtil.div(fmInventory.getTotalPrice(), fmInventory.getInvAvailableNum(), 2);
			fmInventory.setInvNum(ComputeUtil.sub(fmInventory.getInvNum(), fmIn.getQtyOp()));
			fmInventory.setInvAvailableNum(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));

			// 减少库存价值
			fmInventory
					.setTotalPrice(ComputeUtil.sub(fmInventory.getTotalPrice(), ComputeUtil.mul(fmIn.getQtyOp(), avg)));
			this.save(fmInventory);
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		WmInventory toQueryExample = new WmInventory();
		toQueryExample.setSkuCode(toIn.getSkuCode());
		toQueryExample.setLocCode(toIn.getLocCode());
		toQueryExample.setCompanyId(userDetails.getCompanyId());
		toQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> toInventoryList = selectByExample(toQueryExample);
		if (toInventoryList.size() > 0) {
			WmInventory toInventory = toInventoryList.get(0);
			actTran.setToQtyBefore(toInventory.getInvAvailableNum());
			actTran.setToQtyAfter(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));
			toInventory.setInvNum(ComputeUtil.add(toInventory.getInvNum(), toIn.getQtyOp()));
			toInventory.setInvAvailableNum(ComputeUtil.add(toInventory.getInvAvailableNum(), toIn.getQtyOp()));
			// 增加库存价值
			toInventory.setTotalPrice(
					ComputeUtil.add(toInventory.getTotalPrice(), ComputeUtil.mul(toIn.getQtyOp().doubleValue(), avg)));
			this.save(toInventory);
		} else {
			WmInventory toInventory = new WmInventory();
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyAfter(0.0);
			toInventory.setAllocNum(0.0);
			toInventory.setCompanyId(userDetails.getCompanyId());
			toInventory.setWarehouseId(userDetails.getWarehouseId());
			toInventory.setInvAvailableNum(toIn.getQtyOp());
			toInventory.setInvNum(toIn.getQtyOp());
			toInventory.setLocCode(toIn.getLocCode());
			toInventory.setSkuCode(toIn.getSkuCode());
			toInventory.setTotalPrice(ComputeUtil.mul(toIn.getQtyOp().doubleValue(), avg));
			this.save(toInventory);
		}
		return actTran;
	}

	// 组装消耗子件
	private WmActTran assembleSOperation(InventoryUpdateEntity fmIn) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setCost(fmIn.getCost());
		actTran.setPrice(fmIn.getPrice());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			if (fmInventory.getInvAvailableNum().doubleValue() < fmIn.getQtyOp().doubleValue()) {
				throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]可用数不足！");
			} else {
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
				double per = ComputeUtil.div(fmInventory.getTotalPrice(), fmInventory.getInvAvailableNum(), 2);
				fmInventory.setInvNum(ComputeUtil.sub(fmInventory.getInvNum(), fmIn.getQtyOp()));
				fmInventory.setInvAvailableNum(ComputeUtil.sub(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
				fmInventory.setTotalPrice(
						ComputeUtil.sub(fmInventory.getTotalPrice(), ComputeUtil.mul(per, fmIn.getQtyOp())));
				actTran.setCost(ComputeUtil.mul(per, fmIn.getQtyOp()));
				this.save(fmInventory);
			}
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		return actTran;
	}

	// 生成父件
	private WmActTran assembleFOperation(InventoryUpdateEntity fmIn) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setCost(fmIn.getCost());
		actTran.setToSku(fmIn.getSkuCode());
		actTran.setToLot(fmIn.getLotNum());
		actTran.setToLoc(fmIn.getLocCode());
		actTran.setToQtyOp(fmIn.getQtyOp());
		WmInventory fmQueryExample = new WmInventory();
		fmQueryExample.setSkuCode(fmIn.getSkuCode());
		fmQueryExample.setLocCode(fmIn.getLocCode());
		fmQueryExample.setCompanyId(userDetails.getCompanyId());
		fmQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> fmInventoryList = selectByExample(fmQueryExample);
		if (fmInventoryList.size() > 0) {
			WmInventory fmInventory = fmInventoryList.get(0);
			actTran.setToQtyBefore(fmInventory.getInvAvailableNum());
			actTran.setToQtyAfter(ComputeUtil.add(fmInventory.getInvAvailableNum(), fmIn.getQtyOp()));
			// 预组装数大于 生成的父件数
			if (fmInventory.getPreAssembleNum().doubleValue() > fmIn.getQtyOp().doubleValue()) {
				fmInventory.setPreAssembleNum(ComputeUtil.sub(fmInventory.getPreAssembleNum(), fmIn.getQtyOp()));
			} else {

				// 计算实际增加的库存数，需要把预组装数扣除
				double realAssembleNum = ComputeUtil.sub(fmIn.getQtyOp(), fmInventory.getPreAssembleNum());
				fmInventory.setPreAssembleNum(0.0);
				fmInventory.setInvNum(ComputeUtil.add(fmInventory.getInvNum(), realAssembleNum));
				fmInventory.setInvAvailableNum(ComputeUtil.add(fmInventory.getInvAvailableNum(), realAssembleNum));

				// 计算实际增加的库存价值，要把原库存价值扣除预组装数部分
				double realTotalCost = ComputeUtil.mul(ComputeUtil.div(fmIn.getCost(), fmIn.getQtyOp()),
						realAssembleNum);
				fmInventory.setTotalPrice(ComputeUtil.add(fmInventory.getTotalPrice(), realTotalCost));
			}
			this.save(fmInventory);
		} else {
			WmInventory fmInventory = new WmInventory();
			fmInventory.setAllocNum(0.0);
			fmInventory.setCompanyId(userDetails.getCompanyId());
			fmInventory.setInvAvailableNum(fmIn.getQtyOp());
			fmInventory.setInvNum(fmIn.getQtyOp());
			fmInventory.setLocCode(fmIn.getLocCode());
			fmInventory.setSkuCode(fmIn.getSkuCode());
			fmInventory.setTotalPrice(fmIn.getCost());
			fmInventory.setWarehouseId(userDetails.getWarehouseId());
			this.save(fmInventory);
		}
		return actTran;
	}

	private WmActTran cancelPreAssembleAllocOperation(InventoryUpdateEntity fmIn) throws BusinessException {
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setToSku(fmIn.getSkuCode());
		actTran.setToLot(fmIn.getLotNum());
		actTran.setToLoc(fmIn.getLocCode());
		actTran.setToQtyOp(fmIn.getQtyOp());
		List<WmInventoryQueryItem> list = this.selectByKey(fmIn.getSkuCode(), fmIn.getLocCode(), fmIn.getLotNum());
		// 有库存
		if (!list.isEmpty()) {
			WmInventoryQueryItem queryItem = list.get(0);
			WmInventory targetInv = new WmInventory();
			BeanUtils.copyProperties(queryItem, targetInv);
			actTran.setFmQtyBefore(targetInv.getInvAvailableNum());
			actTran.setFmQtyAfter(ComputeUtil.add(targetInv.getInvAvailableNum(), fmIn.getQtyOp()));
			actTran.setToQtyBefore(targetInv.getInvAvailableNum());
			actTran.setToQtyAfter(ComputeUtil.add(targetInv.getInvAvailableNum(), fmIn.getQtyOp()));
			actTran.setPrice(fmIn.getPrice());
			targetInv.setAllocNum(ComputeUtil.sub(targetInv.getAllocNum(), fmIn.getQtyOp()));
			targetInv.setPreAssembleNum(ComputeUtil.sub(targetInv.getPreAssembleNum(), fmIn.getQtyOp()));
			targetInv.setInvNum(
					targetInv.getInvAvailableNum() + targetInv.getAllocNum() - targetInv.getPreAssembleNum());
			this.save(targetInv);
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		return actTran;
	}

	private WmActTran cancelAllocOperation(InventoryUpdateEntity fmIn) throws BusinessException {
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setToSku(fmIn.getSkuCode());
		actTran.setToLot(fmIn.getLotNum());
		actTran.setToLoc(fmIn.getLocCode());
		actTran.setToQtyOp(fmIn.getQtyOp());
		List<WmInventoryQueryItem> list = this.selectByKey(fmIn.getSkuCode(), fmIn.getLocCode(), fmIn.getLotNum());
		// 有库存
		if (!list.isEmpty()) {
			WmInventoryQueryItem queryItem = list.get(0);
			WmInventory targetInv = new WmInventory();
			BeanUtils.copyProperties(queryItem, targetInv);
			actTran.setFmQtyBefore(targetInv.getInvAvailableNum());
			actTran.setFmQtyAfter(ComputeUtil.add(targetInv.getInvAvailableNum(), fmIn.getQtyOp()));
			actTran.setToQtyBefore(targetInv.getInvAvailableNum());
			actTran.setToQtyAfter(ComputeUtil.add(targetInv.getInvAvailableNum(), fmIn.getQtyOp()));
			actTran.setCost(fmIn.getCost());
			actTran.setPrice(fmIn.getPrice());
			targetInv.setAllocNum(ComputeUtil.sub(targetInv.getAllocNum(), fmIn.getQtyOp()));

			targetInv.setInvAvailableNum(ComputeUtil.add(targetInv.getInvAvailableNum(), fmIn.getQtyOp()));
			targetInv.setInvNum(ComputeUtil.add(targetInv.getInvNum(), fmIn.getQtyOp()));
			// 按照原来的成本价格加回库存价值
			targetInv.setTotalPrice(
					ComputeUtil.add(ComputeUtil.mul(fmIn.getCost(), fmIn.getQtyOp()), targetInv.getTotalPrice()));
			this.save(targetInv);
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		return actTran;
	}

	private WmActTran preAssembleAllocOperation(InventoryUpdateEntity fmIn) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyBefore(fmIn.getQtyOpBefore());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setFmQtyAfter(fmIn.getQtyOpAfter());
		actTran.setPrice(fmIn.getPrice());
		actTran.setToSku(fmIn.getSkuCode());
		actTran.setToLot(fmIn.getLotNum());
		actTran.setToLoc(fmIn.getLocCode());
		actTran.setToQtyBefore(fmIn.getQtyOpBefore());
		actTran.setToQtyOp(fmIn.getQtyOp());
		actTran.setToQtyAfter(fmIn.getQtyOpAfter());
		List<WmInventoryQueryItem> list = this.selectByKey(fmIn.getSkuCode(), fmIn.getLocCode(), fmIn.getLotNum());
		WmInventory targetInv = new WmInventory();
		// 有库存
		if (!list.isEmpty()) {
			WmInventoryQueryItem queryItem = list.get(0);
			BeanUtils.copyProperties(queryItem, targetInv);
			// 可用数不增加
			targetInv.setInvAvailableNum(ComputeUtil.add(targetInv.getInvAvailableNum(), 0.0));
			// 增加分配数
			targetInv.setAllocNum(ComputeUtil.add(targetInv.getAllocNum(), fmIn.getQtyOp()));
			// 增加预加工数
			targetInv.setPreAssembleNum(ComputeUtil.add(targetInv.getPreAssembleNum(), fmIn.getQtyOp()));
			// 库存数=可用数+分配数-预加工数
			targetInv.setInvNum(
					targetInv.getInvAvailableNum() + targetInv.getAllocNum() - targetInv.getPreAssembleNum());
		} else {
			targetInv.setCompanyId(userDetails.getCompanyId());
			targetInv.setWarehouseId(userDetails.getWarehouseId());
			targetInv.setSkuCode(fmIn.getSkuCode());
			targetInv.setLocCode(fmIn.getLocCode());
			// 可用数不增加
			targetInv.setInvAvailableNum(0.0);
			// 增加分配数
			targetInv.setAllocNum(fmIn.getQtyOp());
			// 增加预加工数
			targetInv.setPreAssembleNum(fmIn.getQtyOp());
			targetInv.setInvNum(
					targetInv.getInvAvailableNum() + targetInv.getAllocNum() - targetInv.getPreAssembleNum());
			targetInv.setTotalPrice(0.0);
		}
		this.save(targetInv);
		return actTran;
	}

	private WmActTran allocOperation(InventoryUpdateEntity fmIn) throws BusinessException {
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		actTran.setFmSku(fmIn.getSkuCode());
		actTran.setFmLot(fmIn.getLotNum());
		actTran.setFmLoc(fmIn.getLocCode());
		actTran.setFmQtyBefore(fmIn.getQtyOpBefore());
		actTran.setFmQtyOp(fmIn.getQtyOp());
		actTran.setFmQtyAfter(fmIn.getQtyOpAfter());
		actTran.setPrice(fmIn.getPrice());
		actTran.setToSku(fmIn.getSkuCode());
		actTran.setToLot(fmIn.getLotNum());
		actTran.setToLoc(fmIn.getLocCode());
		actTran.setToQtyBefore(fmIn.getQtyOpBefore());
		actTran.setToQtyOp(fmIn.getQtyOp());
		actTran.setToQtyAfter(fmIn.getQtyOpAfter());
		List<WmInventoryQueryItem> list = this.selectByKey(fmIn.getSkuCode(), fmIn.getLocCode(), fmIn.getLotNum());
		// 有库存
		if (!list.isEmpty()) {
			WmInventoryQueryItem queryItem = list.get(0);
			WmInventory targetInv = new WmInventory();
			BeanUtils.copyProperties(queryItem, targetInv);
			// 分配时就计算成本
			Double avg = ComputeUtil.div(targetInv.getTotalPrice(), targetInv.getInvAvailableNum(), 2);
			actTran.setCost(avg);

			targetInv.setAllocNum(ComputeUtil.add(targetInv.getAllocNum(), fmIn.getQtyOp()));
			targetInv.setInvAvailableNum(ComputeUtil.sub(targetInv.getInvAvailableNum(), fmIn.getQtyOp()));
			// 按照加权平均数减少库存价值
			targetInv.setTotalPrice(ComputeUtil.sub(targetInv.getTotalPrice(), ComputeUtil.mul(avg, fmIn.getQtyOp())));
			this.save(targetInv);
		} else {
			throw new BusinessException("商品[" + fmIn.getSkuCode() + "]在库位[" + fmIn.getLocCode() + "]没有库存！");
		}
		return actTran;
	}

	private WmActTran receiveOperation(InventoryUpdateEntity fmIn) {
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setPrice(fmIn.getPrice());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		WmInventory targetInv = new WmInventory();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmInventoryQueryItem> list = this.selectByKey(fmIn.getSkuCode(), fmIn.getLocCode(), fmIn.getLotNum());
		// 原来有库存
		if (!list.isEmpty()) {
			WmInventoryQueryItem queryItem = list.get(0);
			actTran.setFmSku(queryItem.getSkuCode());
			actTran.setFmLot(queryItem.getLot());
			actTran.setFmLoc(queryItem.getLocCode());
			actTran.setFmQtyBefore(queryItem.getInvNum());
			actTran.setFmQtyOp(fmIn.getQtyOp());
			actTran.setFmQtyAfter(ComputeUtil.add(queryItem.getInvNum(), fmIn.getQtyOp()));

			// 设置数量
			actTran.setToQtyBefore(queryItem.getInvNum());
			actTran.setToQtyOp(fmIn.getQtyOp());
			actTran.setToQtyAfter(ComputeUtil.add(queryItem.getInvNum(), fmIn.getQtyOp()));
			actTran.setPrice(fmIn.getPrice());
			BeanUtils.copyProperties(queryItem, targetInv);
			targetInv.setInvNum(ComputeUtil.add(targetInv.getInvNum(), fmIn.getQtyOp()));
			targetInv.setInvAvailableNum(ComputeUtil.add(targetInv.getInvAvailableNum(), fmIn.getQtyOp()));
			// 增加库存价值
			if (WmsCodeMaster.ACT_REC.getCode().equals(fmIn.getActionCode())) {
				// 非退货入库 按照入库价格增加库存价值
				targetInv.setTotalPrice(
						ComputeUtil.add(targetInv.getTotalPrice(), ComputeUtil.mul(fmIn.getPrice(), fmIn.getQtyOp())));
			} else if (WmsCodeMaster.ACT_RE_REC.getCode().equals(fmIn.getActionCode())) {
				// 非退货入库 按照货品成本增加库存价值
				targetInv.setTotalPrice(
						ComputeUtil.add(targetInv.getTotalPrice(), ComputeUtil.mul(fmIn.getCost(), fmIn.getQtyOp())));
			}

		} // 原来没有库存
		else {
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyOp(fmIn.getQtyOp());
			actTran.setToQtyAfter(fmIn.getQtyOp());
			actTran.setPrice(fmIn.getPrice());
			targetInv.setSkuCode(fmIn.getSkuCode());
			targetInv.setLocCode(fmIn.getLocCode());
			targetInv.setLot(fmIn.getLotNum());
			targetInv.setAllocNum(0.0);
			targetInv.setInvNum(fmIn.getQtyOp());
			targetInv.setInvAvailableNum(fmIn.getQtyOp());
			// 增加库存价值
			if (WmsCodeMaster.ACT_REC.getCode().equals(fmIn.getActionCode())) {
				// 非退货入库 按照入库价格增加库存价值
				targetInv.setTotalPrice(ComputeUtil.mul(fmIn.getPrice(), fmIn.getQtyOp()));
			} else if (WmsCodeMaster.ACT_RE_REC.getCode().equals(fmIn.getActionCode())) {
				// 非退货入库 按照货品成本增加库存价值
				targetInv.setTotalPrice(ComputeUtil.mul(fmIn.getCost(), fmIn.getQtyOp()));
			}
			targetInv.setCompanyId(userDetails.getCompanyId());
			targetInv.setWarehouseId(userDetails.getWarehouseId());
		}
		actTran.setToSku(fmIn.getSkuCode());
		actTran.setToLot(fmIn.getLotNum());
		actTran.setToLoc(fmIn.getLocCode());
		this.save(targetInv);
		return actTran;
	}

	private WmActTran cancelReceiveOperation(InventoryUpdateEntity fmIn) throws BusinessException {
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setPrice(fmIn.getPrice());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		WmInventory targetInv = new WmInventory();
		List<WmInventoryQueryItem> list = this.selectByKey(fmIn.getSkuCode(), fmIn.getLocCode(), fmIn.getLotNum());
		// 库存已经不存在
		if (list.isEmpty()) {
			throw new BusinessException("产品[" + fmIn.getSkuCode() + "] 在库位[" + fmIn.getLocCode() + "库存已不存在，无法取消收货");
		} else {
			WmInventoryQueryItem inventoryQueryItem = list.get(0);
			// 库存可用数不足
			if (inventoryQueryItem.getInvAvailableNum() < fmIn.getQtyOp()) {
				throw new BusinessException(
						"产品[" + fmIn.getSkuCode() + "] 在库位[" + fmIn.getLocCode() + "库存可用数不足，无法取消收货");
			} else {
				actTran.setFmSku(inventoryQueryItem.getSkuCode());
				actTran.setFmLot(inventoryQueryItem.getLot());
				actTran.setFmLoc(inventoryQueryItem.getLocCode());
				actTran.setFmQtyBefore(inventoryQueryItem.getInvNum());
				actTran.setFmQtyOp(fmIn.getQtyOp());
				actTran.setFmQtyAfter(inventoryQueryItem.getInvNum() - fmIn.getQtyOp());

				actTran.setToQtyBefore(inventoryQueryItem.getInvNum());
				actTran.setToQtyOp(fmIn.getQtyOp());
				actTran.setToQtyAfter(ComputeUtil.add(inventoryQueryItem.getInvNum(), fmIn.getQtyOp()));
				actTran.setToSku(inventoryQueryItem.getSkuCode());
				actTran.setToLot(inventoryQueryItem.getLot());
				actTran.setToLoc(inventoryQueryItem.getLocCode());
				actTran.setPrice(fmIn.getPrice());
				BeanUtils.copyProperties(inventoryQueryItem, targetInv);
				targetInv.setInvNum(ComputeUtil.sub(targetInv.getInvNum(), fmIn.getQtyOp()));
				targetInv.setInvAvailableNum(ComputeUtil.sub(targetInv.getInvAvailableNum(), fmIn.getQtyOp()));
				targetInv.setTotalPrice(
						ComputeUtil.sub(targetInv.getTotalPrice(), ComputeUtil.mul(fmIn.getPrice(), fmIn.getQtyOp())));
				// 减少库存价值
				// if
				// (WmsCodeMaster.ACT_CANCEL_REC.getCode().equals(fmIn.getActionCode()))
				// {
				// // 非退货入库 按照入库价格减少库存价值
				// targetInv.setTotalPrice(ComputeUtil.sub(targetInv.getTotalPrice(),
				// ComputeUtil.mul(fmIn.getPrice(), fmIn.getQtyOp())));
				// } else if
				// (WmsCodeMaster.ACT_RE_CANCEL_REC.getCode().equals(fmIn.getActionCode()))
				// {
				// // 非退货入库 按照货品成本减少库存价值
				// targetInv.setTotalPrice(ComputeUtil.sub(targetInv.getTotalPrice(),
				// ComputeUtil.mul(fmIn.getCost(), fmIn.getQtyOp())));
				// }

				this.save(targetInv);
				return actTran;
			}
		}
	}

	private WmActTran addActTran(WmActTran actTran) throws BusinessException {
		return wmActTranService.saveActTran(actTran);
	}

	@Override
	public List<WmInventory> selectByExample(WmInventory model) {
		// TODO Auto-generated method stub
		return wmInventoryMapper.selectByExample(model);
	}

	@Override
	public List<WmInventory> getAvailableInvByExample(WmInventory model) {
		// TODO Auto-generated method stub
		return wmInventoryMapper.getAvailableInvByExample(model);
	}

	@Override
	public List<WmInventoryQueryItem> getAvailableInvByPage(Map map) {
		// TODO Auto-generated method stub
		return wmInventoryMapper.getAvailableInvByPage(map);
	}

	@Override
	public Message move(String skuCode, String locCode, String toLoc, Double moveNum) throws BusinessException {
		// TODO Auto-generated method stub
		Message message = new Message();
		InventoryUpdateEntity fmIn = new InventoryUpdateEntity();
		InventoryUpdateEntity toIn = new InventoryUpdateEntity();
		fmIn.setActionCode(WmsCodeMaster.ACT_MOVE.getCode());
		fmIn.setLocCode(locCode);
		fmIn.setLotNum(null);
		fmIn.setQtyOp(moveNum);
		fmIn.setSkuCode(skuCode);
		toIn.setActionCode(WmsCodeMaster.ACT_MOVE.getCode());
		toIn.setLocCode(toLoc);
		toIn.setLotNum(null);
		toIn.setQtyOp(moveNum);
		toIn.setSkuCode(skuCode);
		this.updateInventory(fmIn, toIn);
		message.setCode(200);
		message.setMsg("操作成功！");
		return message;
	}

	@Override
	public Message transfer(String skuCode, String locCode, String toSku, String toLoc, Double transferNum)
			throws BusinessException {
		// TODO Auto-generated method stub
		Message message = new Message();
		InventoryUpdateEntity fmIn = new InventoryUpdateEntity();
		InventoryUpdateEntity toIn = new InventoryUpdateEntity();
		fmIn.setActionCode(WmsCodeMaster.ACT_TRANSFER.getCode());
		fmIn.setLocCode(locCode);
		fmIn.setLotNum(null);
		fmIn.setQtyOp(transferNum);
		fmIn.setSkuCode(skuCode);
		toIn.setActionCode(WmsCodeMaster.ACT_TRANSFER.getCode());
		toIn.setLocCode(toLoc);
		toIn.setLotNum(null);
		toIn.setQtyOp(transferNum);
		toIn.setSkuCode(toSku);
		this.updateInventory(fmIn, toIn);
		message.setCode(200);
		message.setMsg("操作成功！");
		return message;
	}

}
