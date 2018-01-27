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
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmOutboundAllocMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.query.WmOutboundAllocQueryItem;
import com.xibin.wms.service.WmOutboundHeaderService;
import com.xibin.wms.service.WmOutboundUpdateService;
import com.xibin.wms.service.WmInventoryService;
import com.xibin.wms.service.WmOutboundAllocService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmOutboundAllocServiceImpl  extends BaseManagerImpl implements WmOutboundAllocService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmOutboundAllocMapper wmOutboundAllocMapper;
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
	public List<WmOutboundAllocQueryItem> selectByKey(String orderNo,String lineNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmOutboundAllocMapper.selectByKey(orderNo,lineNo,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmOutboundAllocMapper;
	}

	@Override
	public WmOutboundAlloc saveOutboundAlloc(WmOutboundAlloc model){
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		Integer num = 0;
		if(null ==model.getId()||0==model.getId()){
			model.setAllocId(CodeGenerator.getCodeByCurrentTimeAndRandomNum("ACT"));
		}
		return (WmOutboundAlloc)this.save(model);
	}
	
	
	private int [] idListToArray(List<Integer> ids){
		int []  idArray = new int  [ids.size()];
		for(int i =0;i<ids.size();i++){
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
	public Message shipByHeader(String orderNo) throws BusinessException{
		Message message = new Message();
		List<String> errors= new ArrayList<String>();
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(userDetails.getCompanyId());
		headerQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundHeader> headers = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if(headers.size()>0){
			WmOutboundHeader header = headers.get(0);
			if(header.getStatus().equals(WmsCodeMaster.SO_NEW.getCode())||header.getStatus().equals(WmsCodeMaster.SO_PART_ALLOC.getCode())||header.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())){
				throw new BusinessException("出库单["+orderNo+"]没有拣货，不能发货");
			}
			WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
			allocQueryExample.setOrderNo(orderNo);
			allocQueryExample.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
			allocQueryExample.setCompanyId(userDetails.getCompanyId());
			allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
			List<WmOutboundAlloc> allocs = selectByExample(allocQueryExample);
			for(WmOutboundAlloc alloc:allocs){
				try{
					shipByAlloc(alloc,false);
				}catch (BusinessException e) {
					// TODO: handle exception
					errors.add(e.getMessage());
				}
			}
			wmOutboundUpdateService.updateOutboundStatusByOrderNo(orderNo);
			if(errors.size()>0){
				message.setCode(100);
				message.setMsgs(errors);
				message.converMsgsToMsg("");
			}else{
				message.setCode(200);
				message.setMsg("操作成功");
			}
			return message;
		}else{
			throw new BusinessException("数据有误，出库单["+orderNo+"]不存在");
		}
	}
	public Message cancelShipByHeader(String orderNo) throws BusinessException{
		Message message = new Message();
		List<String> errors= new ArrayList<String>();
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(userDetails.getCompanyId());
		headerQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundHeader> headers = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if(headers.size()>0){
			WmOutboundHeader header = headers.get(0);
			if(header.getStatus().equals(WmsCodeMaster.SO_PART_SHIPPING.getCode())||header.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())){
				WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
				allocQueryExample.setOrderNo(orderNo);
				allocQueryExample.setStatus(WmsCodeMaster.SO_FULL_SHIPPING.getCode());
				allocQueryExample.setCompanyId(userDetails.getCompanyId());
				allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
				List<WmOutboundAlloc> allocs = selectByExample(allocQueryExample);
				for(WmOutboundAlloc alloc:allocs){
					try{
						cancelShipByAlloc(alloc,false);
					}catch (BusinessException e) {
						// TODO: handle exception
						errors.add(e.getMessage());
					}
				}
				wmOutboundUpdateService.updateOutboundStatusByOrderNo(orderNo);
				if(errors.size()>0){
					message.setCode(100);
					message.setMsgs(errors);
					message.converMsgsToMsg("");
				}else{
					message.setCode(200);
					message.setMsg("操作成功");
				}
				return message;
				
			}else{
				throw new BusinessException("出库单["+orderNo+"]没有发货，不能取消发货");
			}
		}else{
			throw new BusinessException("数据有误，出库单["+orderNo+"]不存在");
		}
	}
	@Override
	public void shipByAlloc(WmOutboundAlloc alloc,boolean isUpdateOrder) throws BusinessException{
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		if(!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())){
			throw new BusinessException("出库单["+alloc.getOrderNo()+"]分配明细["+alloc.getAllocId()+"]不是完全拣货状态，不能发货！");
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
		//更新库存
		wmInventoryService.updateInventory(entityFm);
		//更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_SHIPPING.getCode());
		alloc.setShipOp(userDetails.getUserId());
		alloc.setShipTime(new Date());
		WmOutboundAlloc currentRecord = this.saveOutboundAlloc(alloc);
		if(isUpdateOrder){
			wmOutboundUpdateService.updateOutboundStatusByAlloc(currentRecord);
		}
		
	}
	@Override
	public void cancelShipByAlloc(WmOutboundAlloc alloc,boolean isUpdateOrder) throws BusinessException{
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		if(!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())){
			throw new BusinessException("出库单["+alloc.getOrderNo()+"]分配明细["+alloc.getAllocId()+"]不是完全发货状态，不能取消发货！");
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
		//更新库存
		wmInventoryService.updateInventory(entityFm);
		//更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
		alloc.setShipOp(userDetails.getUserId());
		alloc.setShipTime(new Date());
		WmOutboundAlloc currentRecord = this.saveOutboundAlloc(alloc);
		if(isUpdateOrder){
			wmOutboundUpdateService.updateOutboundStatusByAlloc(currentRecord);
		}
	}
	@Override
	public void pickByAlloc(WmOutboundAlloc alloc) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		if(!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())){
			throw new BusinessException("出库单["+alloc.getOrderNo()+"]分配明细["+alloc.getAllocId()+"]不是完全分配状态，不能拣货！");
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		entityFm.setActionCode(WmsCodeMaster.ACT_PICK.getCode());
		entityFm.setLineNo(alloc.getLineNo());
		entityFm.setLocCode(alloc.getAllocLocCode());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityFm.setSkuCode(alloc.getSkuCode());
		entityFm.setQtyOp(alloc.getOutboundNum());
		entityFm.setCost(alloc.getCost());
		entityFm.setPrice(alloc.getOutboundPrice());
		InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
		entityTo.setActionCode(WmsCodeMaster.ACT_PICK.getCode());
		entityTo.setLineNo(alloc.getLineNo());
		entityTo.setLocCode(alloc.getToLocCode());
		entityTo.setOrderNo(alloc.getOrderNo());
		entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityTo.setSkuCode(alloc.getSkuCode());
		entityTo.setQtyOp(alloc.getOutboundNum());
		entityTo.setCost(alloc.getCost());
		entityTo.setPrice(alloc.getOutboundPrice());
		//更新库存
		wmInventoryService.updateInventory(entityFm, entityTo);
		//更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
		alloc.setPickOp(userDetails.getUserId());
		alloc.setPickTime(new Date());
		WmOutboundAlloc currentRecord = this.saveOutboundAlloc(alloc);
		wmOutboundUpdateService.updateOutboundStatusByAlloc(currentRecord);
	}
	
	@Override
	public void cancelPickByAlloc(WmOutboundAlloc alloc) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		if(!alloc.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())){
			throw new BusinessException("出库单["+alloc.getOrderNo()+"]分配明细["+alloc.getAllocId()+"]不是完全拣货状态，不能取消拣货！");
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_PICK.getCode());
		entityFm.setLineNo(alloc.getLineNo());
		entityFm.setLocCode(alloc.getToLocCode());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityFm.setSkuCode(alloc.getSkuCode());
		entityFm.setQtyOp(alloc.getOutboundNum());
		entityFm.setCost(alloc.getCost());
		entityFm.setPrice(alloc.getOutboundPrice());
		InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
		entityTo.setActionCode(WmsCodeMaster.ACT_CANCEL_PICK.getCode());
		entityTo.setLineNo(alloc.getLineNo());
		entityTo.setLocCode(alloc.getAllocLocCode());
		entityTo.setOrderNo(alloc.getOrderNo());
		entityTo.setOrderType(WmsCodeMaster.ORDER_OUB.getCode());
		entityTo.setSkuCode(alloc.getSkuCode());
		entityTo.setQtyOp(alloc.getOutboundNum());
		entityTo.setCost(alloc.getCost());
		entityTo.setPrice(alloc.getOutboundPrice());
		//更新库存
		wmInventoryService.updateInventory(entityFm, entityTo);
		//更新分配明细状态
		alloc.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
		alloc.setPickOp(null);
		alloc.setPickTime(null);
		WmOutboundAlloc currentRecord = this.saveOutboundAlloc(alloc);
		wmOutboundUpdateService.updateOutboundStatusByAlloc(currentRecord);
	}

	

}
