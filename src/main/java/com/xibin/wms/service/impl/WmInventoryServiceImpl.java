package com.xibin.wms.service.impl;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.dao.WmInventoryMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmInventoryQueryItem;
import com.xibin.wms.service.WmActTranService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmInventoryService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmInventoryServiceImpl  extends BaseManagerImpl implements WmInventoryService {
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
	public List<WmInventoryQueryItem> selectByKey(String skuCode,String locCode,String lot) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmInventoryMapper.selectByKey(skuCode,locCode,lot,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
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
		if(checkResult !=null){
			throw new BusinessException(checkResult);
		}
		if(WmsCodeMaster.ACT_REC.getCode().equals(actionCode)){
			//收货
			actTran = receiveOperation(fmIn);
		}else if(WmsCodeMaster.ACT_CANCEL_REC.getCode().equals(actionCode)){
			//取消收货
			actTran = cancelReceiveOperation(fmIn);
		}else if(WmsCodeMaster.ACT_ALLOC.getCode().equals(actionCode)){
			actTran = allocOperation(fmIn);
		}else if(WmsCodeMaster.ACT_CANCEL_ALLOC.getCode().equals(actionCode)){
			actTran = cancelAllocOperation(fmIn);
		}else if(WmsCodeMaster.ACT_CANCEL_SHIP.getCode().equals(actionCode)){
			actTran = cancelShipOperation(fmIn);
		}else if(WmsCodeMaster.ACT_SHIP.getCode().equals(actionCode)){
			actTran = shipOperation(fmIn);
		}else{
			throw new BusinessException("操作码["+actionCode+"]有误！");
		}
		//写入交易记录
		return addActTran(actTran);
	}
	@Override
	public WmActTran updateInventory(InventoryUpdateEntity fmIn,InventoryUpdateEntity toIn) throws BusinessException {
		// TODO Auto-generated method stub
		String actionCode = fmIn.getActionCode();
		WmActTran actTran = new WmActTran();
		String checkResult = checkBeforeReceive(fmIn);
		if(checkResult !=null){
			throw new BusinessException(checkResult);
		}
		if(WmsCodeMaster.ACT_PICK.getCode().equals(actionCode)){
			//拣货
			actTran = pickOperation(fmIn,toIn);
		}else if(WmsCodeMaster.ACT_CANCEL_PICK.getCode().equals(actionCode)){
			actTran = cancelPickOperation(fmIn,toIn);
		}else{
			throw new BusinessException("操作码["+actionCode+"]有误！");
		}
		//写入交易记录
		return addActTran(actTran);
	}
	private String checkBeforeReceive(InventoryUpdateEntity entity){
		if(null==entity.getActionCode()||entity.getActionCode().isEmpty()){
			return "库存更新操作码为空！";
		}else if(null==entity.getSkuCode()||entity.getSkuCode().isEmpty()){
			return "库存更新产品编码["+entity.getActionCode()+"]为空！";
		}else if(null==entity.getLocCode()||entity.getLocCode().isEmpty()){
			return "库存更新目标库位编码["+entity.getActionCode()+"]为空！";
		}
		return null;
	}
	private WmActTran shipOperation(InventoryUpdateEntity fmIn) throws BusinessException{
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
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
		if(fmInventoryList.size()>0){
			WmInventory fmInventory = fmInventoryList.get(0);
			if(fmInventory.getInvAvailableNum().doubleValue()<fmIn.getQtyOp().doubleValue()){
				throw new BusinessException("商品["+fmIn.getSkuCode()+"]在库位["+fmIn.getLocCode()+"]可用数不足！");
			}else{
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(fmInventory.getInvAvailableNum()-fmIn.getQtyOp());
				fmInventory.setInvNum(fmInventory.getInvNum() - fmIn.getQtyOp());
				Double oldAvailableNum = fmInventory.getInvAvailableNum();
				fmInventory.setInvAvailableNum(oldAvailableNum-fmIn.getQtyOp());
				
				//按照计算的成本价格减少库存价值
				fmInventory.setTotalPrice(fmInventory.getTotalPrice()-fmIn.getQtyOp()*fmIn.getCost());
				
				this.save(fmInventory);
			}
		}else{
			throw new BusinessException("商品["+fmIn.getSkuCode()+"]在库位["+fmIn.getLocCode()+"]没有库存！");
		}
		return actTran;
	}
	private WmActTran cancelShipOperation(InventoryUpdateEntity toIn) throws BusinessException{
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
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
		if(fmInventoryList.size()>0){
			WmInventory fmInventory = fmInventoryList.get(0);
			actTran.setToQtyBefore(fmInventory.getInvAvailableNum());
			actTran.setToQtyAfter(fmInventory.getInvAvailableNum()+toIn.getQtyOp());
			fmInventory.setInvNum(fmInventory.getInvNum() + toIn.getQtyOp());
			fmInventory.setInvAvailableNum(fmInventory.getInvAvailableNum()+toIn.getQtyOp());
			//增加库存价值
			fmInventory.setTotalPrice(fmInventory.getTotalPrice()+toIn.getQtyOp()*toIn.getCost());
			this.save(fmInventory);
		}else{
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
			//增加库存价值
			toInventory.setTotalPrice(toIn.getQtyOp()*toIn.getCost());
			this.save(toInventory);
		}
		return actTran;
	}
	private WmActTran pickOperation(InventoryUpdateEntity fmIn,InventoryUpdateEntity toIn) throws BusinessException{
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
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
		if(fmInventoryList.size()>0){
			WmInventory fmInventory = fmInventoryList.get(0);
			if(fmInventory.getAllocNum().doubleValue()<fmIn.getQtyOp().doubleValue()){
				throw new BusinessException("商品["+fmIn.getSkuCode()+"]在库位["+fmIn.getLocCode()+"]已分配数不足！");
			}else{
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(fmInventory.getInvAvailableNum());
				fmInventory.setInvNum(fmInventory.getInvNum() - fmIn.getQtyOp());
				fmInventory.setAllocNum(fmInventory.getAllocNum() - fmIn.getQtyOp());
				this.save(fmInventory);
			}
		}else{
			throw new BusinessException("商品["+fmIn.getSkuCode()+"]在库位["+fmIn.getLocCode()+"]没有库存！");
		}
		WmInventory toQueryExample = new WmInventory();
		toQueryExample.setSkuCode(toIn.getSkuCode());
		toQueryExample.setLocCode(toIn.getLocCode());
		toQueryExample.setCompanyId(userDetails.getCompanyId());
		toQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> toInventoryList = selectByExample(toQueryExample);
		if(toInventoryList.size()>0){
			WmInventory toInventory = toInventoryList.get(0);
			actTran.setToQtyBefore(toInventory.getInvAvailableNum());
			actTran.setToQtyAfter(toInventory.getInvAvailableNum()+toIn.getQtyOp());
			toInventory.setInvNum(toInventory.getInvNum()+toIn.getQtyOp());
			toInventory.setInvAvailableNum(toInventory.getInvAvailableNum()+toIn.getQtyOp());
			
			//库存价值增加
			toInventory.setTotalPrice(toInventory.getTotalPrice()+toIn.getQtyOp()*fmIn.getCost());
			this.save(toInventory);
		}else{
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
			//库存价值增加
			toInventory.setTotalPrice(toIn.getQtyOp()*fmIn.getCost());
			this.save(toInventory);
		}
		return actTran;
	}
	private WmActTran cancelPickOperation(InventoryUpdateEntity fmIn,InventoryUpdateEntity toIn) throws BusinessException{
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
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
		if(fmInventoryList.size()>0){
			WmInventory fmInventory = fmInventoryList.get(0);
			if(fmInventory.getInvAvailableNum().doubleValue()<fmIn.getQtyOp().doubleValue()){
				throw new BusinessException("商品["+fmIn.getSkuCode()+"]在库位["+fmIn.getLocCode()+"]可用数不足！");
			}else{
				actTran.setFmQtyBefore(fmInventory.getInvAvailableNum());
				actTran.setFmQtyAfter(fmInventory.getInvAvailableNum()-fmIn.getQtyOp());
				fmInventory.setInvNum(fmInventory.getInvNum() - fmIn.getQtyOp());
				fmInventory.setInvAvailableNum(fmInventory.getInvAvailableNum() - fmIn.getQtyOp());
				//减少库存价值
				fmInventory.setTotalPrice(fmInventory.getTotalPrice() - fmIn.getQtyOp()*fmIn.getCost());
				this.save(fmInventory);
			}
		}else{
			throw new BusinessException("商品["+fmIn.getSkuCode()+"]在库位["+fmIn.getLocCode()+"]没有库存！");
		}
		WmInventory toQueryExample = new WmInventory();
		toQueryExample.setSkuCode(toIn.getSkuCode());
		toQueryExample.setLocCode(toIn.getLocCode());
		toQueryExample.setCompanyId(userDetails.getCompanyId());
		toQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInventory> toInventoryList = selectByExample(toQueryExample);
		if(toInventoryList.size()>0){
			WmInventory toInventory = toInventoryList.get(0);
			actTran.setToQtyBefore(toInventory.getInvAvailableNum());
			actTran.setToQtyAfter(toInventory.getInvAvailableNum());
			toInventory.setInvNum(toInventory.getInvNum()+toIn.getQtyOp());
			toInventory.setAllocNum(toInventory.getAllocNum()+toIn.getQtyOp());
			this.save(toInventory);
		}else{
			WmInventory toInventory = new WmInventory();
			actTran.setToQtyBefore(0.0);
			actTran.setToQtyAfter(0.0);
			toInventory.setAllocNum(toIn.getQtyOp());
			toInventory.setCompanyId(userDetails.getCompanyId());
			toInventory.setWarehouseId(userDetails.getWarehouseId());
			toInventory.setInvAvailableNum(0.0);
			toInventory.setInvNum(toIn.getQtyOp());
			toInventory.setLocCode(toIn.getLocCode());
			toInventory.setSkuCode(toIn.getSkuCode());
			this.save(toInventory);
		}
		return actTran;
	}
	private WmActTran cancelAllocOperation(InventoryUpdateEntity fmIn) throws BusinessException{
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
		//有库存
		if(!list.isEmpty()){
			WmInventoryQueryItem queryItem = list.get(0);
			WmInventory targetInv = new WmInventory();
			BeanUtils.copyProperties(queryItem, targetInv);
			actTran.setFmQtyBefore(targetInv.getInvAvailableNum());
			actTran.setFmQtyAfter(targetInv.getInvAvailableNum()+fmIn.getQtyOp());
			actTran.setToQtyBefore(targetInv.getInvAvailableNum());
			actTran.setToQtyAfter(targetInv.getInvAvailableNum()+fmIn.getQtyOp());
			actTran.setCost(fmIn.getCost());
			actTran.setPrice(fmIn.getPrice());
			targetInv.setAllocNum(targetInv.getAllocNum()-fmIn.getQtyOp());
			targetInv.setInvAvailableNum(targetInv.getInvAvailableNum()+fmIn.getQtyOp());
			//按照原来的成本价格加回库存价值
			targetInv.setTotalPrice(fmIn.getCost()*fmIn.getQtyOp()+targetInv.getTotalPrice());
			this.save(targetInv);
		}else{
			throw new BusinessException("商品["+fmIn.getSkuCode()+"]在库位["+fmIn.getLocCode()+"]没有库存！");
		}
		return actTran;
	}
	private WmActTran allocOperation(InventoryUpdateEntity fmIn) throws BusinessException{
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
		//有库存
		if(!list.isEmpty()){
			WmInventoryQueryItem queryItem = list.get(0);
			WmInventory targetInv = new WmInventory();
			BeanUtils.copyProperties(queryItem, targetInv);
			//分配时就计算成本
			Double avg = targetInv.getTotalPrice()/targetInv.getInvAvailableNum();
			actTran.setCost(avg);
			
			targetInv.setAllocNum(targetInv.getAllocNum()+fmIn.getQtyOp());
			targetInv.setInvAvailableNum(targetInv.getInvAvailableNum()-fmIn.getQtyOp());
			//按照加权平均数减少库存价值
			targetInv.setTotalPrice(targetInv.getTotalPrice()-avg*fmIn.getQtyOp());
			this.save(targetInv);
		}else{
			throw new BusinessException("商品["+fmIn.getSkuCode()+"]在库位["+fmIn.getLocCode()+"]没有库存！");
		}
		return actTran;
	}
	private WmActTran receiveOperation(InventoryUpdateEntity fmIn){
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setPrice(fmIn.getPrice());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		WmInventory targetInv = new WmInventory();
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmInventoryQueryItem> list = this.selectByKey(fmIn.getSkuCode(), fmIn.getLocCode(), fmIn.getLotNum());
		//原来有库存
		if(!list.isEmpty()){
			WmInventoryQueryItem queryItem = list.get(0);
			actTran.setFmSku(queryItem.getSkuCode());
			actTran.setFmLot(queryItem.getLot());
			actTran.setFmLoc(queryItem.getLocCode());
			actTran.setFmQtyBefore(queryItem.getInvNum());
			actTran.setFmQtyOp(fmIn.getQtyOp());
			actTran.setFmQtyAfter(queryItem.getInvNum()+fmIn.getQtyOp());
			
			//设置数量
			actTran.setToQtyBefore(queryItem.getInvNum());
			actTran.setToQtyOp(fmIn.getQtyOp());
			actTran.setToQtyAfter(queryItem.getInvNum()+fmIn.getQtyOp());
			actTran.setPrice(fmIn.getPrice());
			BeanUtils.copyProperties(queryItem, targetInv);
			targetInv.setInvNum(targetInv.getInvNum()+fmIn.getQtyOp());
			targetInv.setInvAvailableNum(targetInv.getInvAvailableNum()+fmIn.getQtyOp());
			//增加库存价值
			targetInv.setTotalPrice(targetInv.getTotalPrice()+fmIn.getPrice()*fmIn.getQtyOp());
		}//原来没有库存
		else{
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
			//增加库存价值
			targetInv.setTotalPrice(fmIn.getPrice()*fmIn.getQtyOp());
			targetInv.setCompanyId(userDetails.getCompanyId());
			targetInv.setWarehouseId(userDetails.getWarehouseId());
		}
		actTran.setToSku(fmIn.getSkuCode());
		actTran.setToLot(fmIn.getLotNum());
		actTran.setToLoc(fmIn.getLocCode());
		this.save(targetInv);
		return actTran;
	}
	
	private WmActTran cancelReceiveOperation(InventoryUpdateEntity fmIn) throws BusinessException{
		WmActTran actTran = new WmActTran();
		actTran.setLineNo(fmIn.getLineNo());
		actTran.setOrderNo(fmIn.getOrderNo());
		actTran.setPrice(fmIn.getPrice());
		actTran.setOrderType(fmIn.getOrderType());
		actTran.setTranType(fmIn.getActionCode());
		actTran.setTranTime(new Date());
		WmInventory targetInv = new WmInventory();
		List<WmInventoryQueryItem> list = this.selectByKey(fmIn.getSkuCode(), fmIn.getLocCode(), fmIn.getLotNum());
		//库存已经不存在
		if(list.isEmpty()){
			throw new BusinessException("产品["+fmIn.getSkuCode()+"] 在库位["+fmIn.getLocCode()+"库存已不存在，无法取消收货");
		}else{
			WmInventoryQueryItem inventoryQueryItem = list.get(0);
			//库存可用数不足
			if(inventoryQueryItem.getInvAvailableNum()<fmIn.getQtyOp()){
				throw new BusinessException("产品["+fmIn.getSkuCode()+"] 在库位["+fmIn.getLocCode()+"库存可用数不足，无法取消收货");
			}else{
				actTran.setFmSku(inventoryQueryItem.getSkuCode());
				actTran.setFmLot(inventoryQueryItem.getLot());
				actTran.setFmLoc(inventoryQueryItem.getLocCode());
				actTran.setFmQtyBefore(inventoryQueryItem.getInvNum());
				actTran.setFmQtyOp(fmIn.getQtyOp());
				actTran.setFmQtyAfter(inventoryQueryItem.getInvNum()-fmIn.getQtyOp());
					
				actTran.setToQtyBefore(inventoryQueryItem.getInvNum());
				actTran.setToQtyOp(fmIn.getQtyOp());
				actTran.setToQtyAfter(inventoryQueryItem.getInvNum()+fmIn.getQtyOp());
				actTran.setToSku(inventoryQueryItem.getSkuCode());
				actTran.setToLot(inventoryQueryItem.getLot());
				actTran.setToLoc(inventoryQueryItem.getLocCode());
				actTran.setPrice(fmIn.getPrice());
				BeanUtils.copyProperties(inventoryQueryItem, targetInv);
				targetInv.setInvNum(targetInv.getInvNum()-fmIn.getQtyOp());
				targetInv.setInvAvailableNum(targetInv.getInvAvailableNum()-fmIn.getQtyOp());
				//减少库存价值
				targetInv.setTotalPrice(targetInv.getTotalPrice()-fmIn.getPrice()*fmIn.getQtyOp());
				this.save(targetInv);
				return actTran;
			}
		}
	}
	private WmActTran addActTran(WmActTran actTran) throws BusinessException{
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


}
