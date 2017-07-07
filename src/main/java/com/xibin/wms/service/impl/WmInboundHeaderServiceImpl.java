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
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.service.WmInboundHeaderService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmInboundHeaderServiceImpl  extends BaseManagerImpl implements WmInboundHeaderService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmInboundHeaderMapper wmInboundHeaderMapper;
	@Override
	public WmInboundHeader getInboundOrderById(int id) {
		// TODO Auto-generated method stub
		return wmInboundHeaderMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmInboundHeaderQueryItem> getAllInboundOrderByPage(Map map) {
		// TODO Auto-generated method stub
		return wmInboundHeaderMapper.selectAllByPage(map);
	}

	@Override
	public List<WmInboundHeaderQueryItem> selectByKey(String orderNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmInboundHeaderMapper.selectByKey(orderNo,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmInboundHeaderMapper;
	}

	@Override
	public WmInboundHeaderQueryItem saveInbound(WmInboundHeader model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if(null == model.getId()||model.getId() == 0){
			model.setOrderNo(CodeGenerator.getCodeByCurrentTimeAndRandomNum("INB"));
		}
		this.save(model);
		List<WmInboundHeaderQueryItem> list = selectByKey(model.getOrderNo());
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<WmInboundHeader> selectByExample(WmInboundHeader model) {
		// TODO Auto-generated method stub
		return wmInboundHeaderMapper.selectByExample(model);
	}
	@Override
	public int remove(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if(queryResult.size()>0){
			WmInboundHeader wmInboundHeader = new WmInboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
			if(WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmInboundHeader.getAuditStatus())){
				throw new BusinessException("入库单["+wmInboundHeader.getOrderNo()+"]已审核");
			}else if(!WmsCodeMaster.INB_NEW.getCode().equals(wmInboundHeader.getStatus())){
				throw new BusinessException("入库单["+wmInboundHeader.getOrderNo()+"]不是创建状态");
			}
			return this.delete(wmInboundHeader.getId());
		}else{
			throw new BusinessException("入库单["+orderNo+"]不存在");
		}
	}
	@Override
	public WmInboundHeaderQueryItem audit(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if(queryResult.size()>0){
			WmInboundHeader wmInboundHeader = new WmInboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
			if(WmsCodeMaster.AUDIT_CLOSE.getCode().equals(wmInboundHeader.getAuditStatus())){
				throw new BusinessException("入库单["+wmInboundHeader.getOrderNo()+"]已审核");
			}else if(!WmsCodeMaster.INB_NEW.getCode().equals(wmInboundHeader.getStatus())){
				throw new BusinessException("入库单["+wmInboundHeader.getOrderNo()+"]不是创建状态");
			}
			wmInboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_CLOSE.getCode());
			wmInboundHeader.setAuditOp(userDetails.getUserId());
			wmInboundHeader.setAuditTime(new Date());
			return  this.saveInbound(wmInboundHeader);
		}else{
			throw new BusinessException("入库单["+orderNo+"]不存在");
		}
	}
	@Override
	public WmInboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		List<WmInboundHeaderQueryItem> queryResult = this.selectByKey(orderNo);
		if(queryResult.size()>0){
			WmInboundHeader wmInboundHeader = new WmInboundHeader();
			BeanUtils.copyProperties(queryResult.get(0), wmInboundHeader);
			if(WmsCodeMaster.AUDIT_NEW.getCode().equals(wmInboundHeader.getAuditStatus())){
				throw new BusinessException("入库单["+wmInboundHeader.getOrderNo()+"]未审核");
			}else if(!WmsCodeMaster.INB_NEW.getCode().equals(wmInboundHeader.getStatus())){
				throw new BusinessException("入库单["+wmInboundHeader.getOrderNo()+"]不是创建状态");
			}
			wmInboundHeader.setAuditStatus(WmsCodeMaster.AUDIT_NEW.getCode());
			wmInboundHeader.setAuditOp(null);
			wmInboundHeader.setAuditTime(null);
			return  this.saveInbound(wmInboundHeader);
		}else{
			throw new BusinessException("入库单["+orderNo+"]不存在");
		}
	}

}
