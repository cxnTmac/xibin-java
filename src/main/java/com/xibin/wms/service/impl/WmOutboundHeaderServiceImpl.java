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
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.dao.WmOutboundHeaderMapper;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmOutboundHeaderService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmOutboundHeaderServiceImpl  extends BaseManagerImpl implements WmOutboundHeaderService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmOutboundHeaderMapper wmOutboundHeaderMapper;
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
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmOutboundHeaderMapper.selectByKey(orderNo,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmOutboundHeaderMapper;
	}

	@Override
	public WmOutboundHeaderQueryItem saveOutbound(WmOutboundHeader model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if(null == model.getId()||model.getId() == 0){
			model.setOrderNo(CodeGenerator.getCodeByCurrentTimeAndRandomNum("OUB"));
		}
		this.save(model);
		List<WmOutboundHeaderQueryItem> list = selectByKey(model.getOrderNo());
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	@Override
	public int remove(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if(queryResult.size()>0){
			WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
			if(WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmOutboundHeader.getAuditStatus())){
				throw new BusinessException("入库单["+wmOutboundHeader.getOrderNo()+"]已审核");
			}else if(!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())){
				throw new BusinessException("入库单["+wmOutboundHeader.getOrderNo()+"]不是创建状态");
			}
			return this.delete(wmOutboundHeader.getId());
		}else{
			throw new BusinessException("入库单["+orderNo+"]不存在");
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
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if(queryResult.size()>0){
			WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
			if(WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmOutboundHeader.getAuditStatus())){
				throw new BusinessException("出库单["+wmOutboundHeader.getOrderNo()+"]已审核");
			}else if(!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())){
				throw new BusinessException("出库单["+wmOutboundHeader.getOrderNo()+"]不是创建状态");
			}
			wmOutboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_CLOSE.getCode());
			wmOutboundHeader.setAuditOp(userDetails.getUserId());
			wmOutboundHeader.setAuditTime(new Date());
			return this.saveOutbound(wmOutboundHeader);
		}else{
			throw new BusinessException("出库单["+orderNo+"]不存在");
		}
	}
	@Override
	public WmOutboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		List<WmOutboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if(queryResult.size()>0){
			WmOutboundHeader wmOutboundHeader = new WmOutboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmOutboundHeader);
			if(WmsCodeMaster.AUDIT_NEW.getCode().equals(wmOutboundHeader.getAuditStatus())){
				throw new BusinessException("出库单["+wmOutboundHeader.getOrderNo()+"]未审核");
			}else if(!WmsCodeMaster.INB_NEW.getCode().equals(wmOutboundHeader.getStatus())){
				throw new BusinessException("出库单["+wmOutboundHeader.getOrderNo()+"]不是创建状态");
			}
			wmOutboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_NEW.getCode());
			wmOutboundHeader.setAuditOp(null);
			wmOutboundHeader.setAuditTime(null);
			return this.saveOutbound(wmOutboundHeader);
		}else{
			throw new BusinessException("出库单["+orderNo+"]不存在");
		}
	}

}
