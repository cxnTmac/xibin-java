package com.xibin.wms.service.impl;

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
import com.xibin.wms.dao.WmAssembleFDetailMapper;
import com.xibin.wms.dao.WmAssembleHeaderMapper;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.pojo.WmAssembleFDetail;
import com.xibin.wms.pojo.WmAssembleHeader;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.query.WmAssembleFDetailQueryItem;
import com.xibin.wms.query.WmInboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.service.WmAssembleFDetailService;
import com.xibin.wms.service.WmAssembleHeaderService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmToFinService;

import sun.awt.ModalExclude;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmAssembleFDetailServiceImpl  extends BaseManagerImpl implements WmAssembleFDetailService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmAssembleHeaderService wmAssembleHeaderService;
	@Resource
	private WmAssembleFDetailMapper wmAssembleFDetailMapper;


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
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId());
		map.put("warehouseId", userDetails.getWarehouseId());
		return wmAssembleFDetailMapper.selectAllByOrderNo(map);
	}

	@Override
	public List<WmAssembleFDetail> selectByKey(String orderNo, String lineNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmAssembleFDetailMapper.selectByKey(orderNo, lineNo, userDetails.getCompanyId().toString(), userDetails.getWarehouseId().toString());
	}

	@Override
	public List<WmAssembleFDetail> selectByExample(WmAssembleFDetail model) {
		// TODO Auto-generated method stub
		return wmAssembleFDetailMapper.selectByExample(model);
	}

	@Override
	public WmAssembleFDetail saveAssembleFDetail(WmAssembleFDetail model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		if(null ==model.getId()||0==model.getId()){
			List<Integer> lastLineNo = wmAssembleFDetailMapper.selectLastLineNo(model.getOrderNo(), model.getCompanyId().toString(), model.getWarehouseId().toString());
				Integer num = lastLineNo.get(0);
				if(num == null){
					model.setLineNo("1");
				}else{
					num++;
					model.setLineNo(num+"");
				}
		}
		
		this.save(model);
		List<WmAssembleFDetail> list = selectByKey(model.getOrderNo(),model.getLineNo());
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public int remove(String orderNo, String lineNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		Message message = operateBeforeCheck(orderNo);
		
		if(message.getCode()==0){
			throw new BusinessException(message.getMsg());
		}else{
			List<WmAssembleFDetail> list = selectByKey(orderNo,lineNo);
			if(list.size()>0){
				Integer idInteger = list.get(0).getId();
				return this.delete(idInteger);
			}else{
				return 0;
			}
		}
	}
	
	
	private Message operateBeforeCheck(String orderNo){
		Message message = new Message();
		List<WmAssembleHeader> headerList = wmAssembleHeaderService.selectByKey(orderNo);
		
		if(headerList.size()>0){
			WmAssembleHeader header = headerList.get(0);
			//已审核或不审核
			if(WmsCodeMaster.AUDIT_CLOSE.getCode().equals(header.getAuditStatus())){
				message.setCode(0);
				message.setMsg("组装单["+orderNo+"]已审核,不能对明细进行编辑");
				return message;
			}else if(!WmsCodeMaster.INB_NEW.getCode().equals(header.getStatus())){
				message.setCode(0);
				message.setMsg("组装单["+orderNo+"]不是创建状态,不能对明细进行编辑");
				return message;
			}else{
				message.setCode(200);
				return message;
			}
		}
		message.setCode(0);
		message.setMsg("组装单["+orderNo+"]数据丢失,请联系管理员");
		return message;
	}
}
