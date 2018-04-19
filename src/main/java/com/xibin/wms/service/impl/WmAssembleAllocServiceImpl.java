package com.xibin.wms.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.sun.org.apache.bcel.internal.generic.ReturnaddressType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart;
import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmAssembleAllocMapper;
import com.xibin.wms.dao.WmAssembleFDetailMapper;
import com.xibin.wms.dao.WmAssembleHeaderMapper;
import com.xibin.wms.dao.WmAssembleSDetailMapper;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmAssembleAlloc;
import com.xibin.wms.pojo.WmAssembleFDetail;
import com.xibin.wms.pojo.WmAssembleHeader;
import com.xibin.wms.pojo.WmAssembleSDetail;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.query.BdFittingSkuAssembleQueryItem;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.query.WmAssembleAllocQueryItem;
import com.xibin.wms.query.WmAssembleFDetailQueryItem;
import com.xibin.wms.query.WmAssembleSDetailQueryItem;
import com.xibin.wms.query.WmInboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.service.BdFittingSkuAssembleService;
import com.xibin.wms.service.BdFittingSkuService;
import com.xibin.wms.service.WmAssembleAllocService;
import com.xibin.wms.service.WmAssembleFDetailService;
import com.xibin.wms.service.WmAssembleHeaderService;
import com.xibin.wms.service.WmAssembleSDetailService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmInventoryService;
import com.xibin.wms.service.WmToFinService;

import sun.awt.ModalExclude;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmAssembleAllocServiceImpl  extends BaseManagerImpl implements WmAssembleAllocService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmAssembleAllocMapper wmAssembleAllocMapper;
	@Resource
	private WmAssembleSDetailService wmAssembleSDetailService;
	@Resource
	private WmInventoryService wmInventoryService;


	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmAssembleAllocMapper;
	}

	@Override
	public WmAssembleAlloc getAssembleAllocById(int id) {
		// TODO Auto-generated method stub
		return wmAssembleAllocMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmAssembleAllocQueryItem> getAllAssembleAllocByOrderNo(Map map) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId());
		map.put("warehouseId", userDetails.getWarehouseId());
		return wmAssembleAllocMapper.selectAllByOrderNo(map);
	}

	@Override
	public List<WmAssembleAlloc> selectByKey(String allocId) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmAssembleAllocMapper.selectByKey(allocId,userDetails.getCompanyId().toString(), userDetails.getWarehouseId().toString());
	}

	@Override
	public List<WmAssembleAlloc> selectByExample(WmAssembleAlloc model) {
		// TODO Auto-generated method stub
		return wmAssembleAllocMapper.selectByExample(model);
	}

	@Override
	public WmAssembleAlloc saveAssembleAlloc(WmAssembleAlloc model) throws BusinessException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if(null ==model.getId()||0==model.getId()){
			model.setAllocId(CodeGenerator.getCodeByCurrentTimeAndRandomNum("ACT"));
		}
		return (WmAssembleAlloc)this.save(model);
	}

	
	@Override
	public Message  pickByAlloc(WmAssembleAlloc alloc) throws BusinessException {
		// TODO Auto-generated method stub
		Message message = new Message();
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		if(!alloc.getStatus().equals(WmsCodeMaster.ASS_FULL_ALLOC.getCode())){
			throw new BusinessException("组装单["+alloc.getOrderNo()+"]子件分配明细["+alloc.getAllocId()+"]不是完全分配状态，不能拣货！");
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		entityFm.setActionCode(WmsCodeMaster.ACT_PICK.getCode());
		entityFm.setLineNo(alloc.getsLineNo());
		entityFm.setLocCode(alloc.getAllocLoc());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_ASS.getCode());
		entityFm.setSkuCode(alloc.getFittingSkuCode());
		entityFm.setQtyOp(alloc.getAllocNum());
		entityFm.setCost(alloc.getCost());
		InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
		entityTo.setActionCode(WmsCodeMaster.ACT_PICK.getCode());
		entityTo.setLineNo(alloc.getsLineNo());
		entityTo.setLocCode(alloc.getToLoc());
		entityTo.setOrderNo(alloc.getOrderNo());
		entityTo.setOrderType(WmsCodeMaster.ORDER_ASS.getCode());
		entityTo.setSkuCode(alloc.getFittingSkuCode());
		entityTo.setQtyOp(alloc.getAllocNum());
		entityTo.setCost(alloc.getCost());
		//更新库存
		wmInventoryService.updateInventory(entityFm, entityTo);
		//更新分配明细状态
		alloc.setStatus(WmsCodeMaster.ASS_FULL_PICK.getCode());
		alloc.setPickOp(userDetails.getUserId());
		alloc.setPickTime(new Date());
		this.saveAssembleAlloc(alloc);
		//更新子件明细
		WmAssembleSDetail queryExample = new WmAssembleSDetail();
		queryExample.setOrderNo(alloc.getOrderNo());
		queryExample.setLineNo(alloc.getsLineNo());
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmAssembleSDetail> queryResults = wmAssembleSDetailService.selectByExample(queryExample);
		if(queryResults.size() == 0){
			throw new BusinessException("组装单["+alloc.getOrderNo()+"]行号为["+alloc.getsLineNo()+"]的子件明细不存在，请联系管理员");
		}
		WmAssembleSDetail sDetail = queryResults.get(0);
		sDetail.setPickNum(sDetail.getPickNum().doubleValue()+alloc.getAllocNum().doubleValue());
		if(sDetail.getPickNum().doubleValue() == sDetail.getAllocNum().doubleValue()){
			sDetail.setStatus(WmsCodeMaster.ASS_FULL_PICK.getCode());
		}
		if(sDetail.getPickNum().doubleValue() < sDetail.getAllocNum().doubleValue()){
			sDetail.setStatus(WmsCodeMaster.ASS_PART_PICK.getCode());
		}
		wmAssembleSDetailService.saveAssembleSDetail(sDetail);
		message.setCode(200);
		message.setMsg("操作成功");
		return message;
	}
	
	@Override
	public Message cancelPickByAlloc(WmAssembleAlloc alloc) throws BusinessException {
		// TODO Auto-generated method stub
		Message message = new Message();
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		if(!alloc.getStatus().equals(WmsCodeMaster.ASS_FULL_PICK.getCode())){
			throw new BusinessException("组装单["+alloc.getOrderNo()+"]子件分配明细["+alloc.getAllocId()+"]不是完全拣货状态，不能取消拣货！");
		}
		InventoryUpdateEntity entityFm = new InventoryUpdateEntity();
		entityFm.setActionCode(WmsCodeMaster.ACT_CANCEL_PICK.getCode());
		entityFm.setLineNo(alloc.getsLineNo());
		entityFm.setLocCode(alloc.getToLoc());
		entityFm.setOrderNo(alloc.getOrderNo());
		entityFm.setOrderType(WmsCodeMaster.ORDER_ASS.getCode());
		entityFm.setSkuCode(alloc.getFittingSkuCode());
		entityFm.setQtyOp(alloc.getAllocNum());
		entityFm.setCost(alloc.getCost());
		InventoryUpdateEntity entityTo = new InventoryUpdateEntity();
		entityTo.setActionCode(WmsCodeMaster.ACT_CANCEL_PICK.getCode());
		entityTo.setLineNo(alloc.getsLineNo());
		entityTo.setLocCode(alloc.getAllocLoc());
		entityTo.setOrderNo(alloc.getOrderNo());
		entityTo.setOrderType(WmsCodeMaster.ORDER_ASS.getCode());
		entityTo.setSkuCode(alloc.getFittingSkuCode());
		entityTo.setQtyOp(alloc.getAllocNum());
		entityTo.setCost(alloc.getCost());
		//更新库存
		wmInventoryService.updateInventory(entityFm, entityTo);
		//更新分配明细状态
		alloc.setStatus(WmsCodeMaster.ASS_FULL_ALLOC.getCode());
		alloc.setPickOp(null);
		alloc.setPickTime(null);
		this.saveAssembleAlloc(alloc);
		//更新子件明细
		WmAssembleSDetail queryExample = new WmAssembleSDetail();
		queryExample.setOrderNo(alloc.getOrderNo());
		queryExample.setLineNo(alloc.getsLineNo());
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmAssembleSDetail> queryResults = wmAssembleSDetailService.selectByExample(queryExample);
		if(queryResults.size() == 0){
			throw new BusinessException("组装单["+alloc.getOrderNo()+"]行号为["+alloc.getsLineNo()+"]的子件明细不存在，请联系管理员");
		}
		WmAssembleSDetail sDetail = queryResults.get(0);
		sDetail.setPickNum(sDetail.getPickNum().doubleValue()-alloc.getAllocNum().doubleValue());
		if(sDetail.getPickNum().doubleValue() == 0){
			if(sDetail.getAllocNum() == sDetail.getNum()){
				sDetail.setStatus(WmsCodeMaster.ASS_FULL_ALLOC.getCode());
			}else if(sDetail.getAllocNum() < sDetail.getNum()){
				sDetail.setStatus(WmsCodeMaster.ASS_PART_ALLOC.getCode());
			}
		}
		else{
			sDetail.setStatus(WmsCodeMaster.ASS_PART_PICK.getCode());
		}
		wmAssembleSDetailService.saveAssembleSDetail(sDetail);
		message.setCode(200);
		message.setMsg("操作成功");
		return message;
	}
	
}
