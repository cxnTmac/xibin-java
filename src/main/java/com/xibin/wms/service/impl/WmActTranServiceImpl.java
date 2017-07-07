package com.xibin.wms.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.wms.dao.BdAreaMapper;
import com.xibin.wms.dao.BdLocMapper;
import com.xibin.wms.dao.BdZoneMapper;
import com.xibin.wms.dao.WmActTranMapper;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.BdLoc;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.query.BdLocQueryItem;
import com.xibin.wms.query.BdZoneQueryItem;
import com.xibin.wms.query.WmActTranQueryItem;
import com.xibin.wms.service.BdAreaService;
import com.xibin.wms.service.BdLocService;
import com.xibin.wms.service.BdZoneService;
import com.xibin.wms.service.WmActTranService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmActTranServiceImpl extends BaseManagerImpl implements WmActTranService {
	@Autowired
	private HttpSession session;
	@Autowired
	private WmActTranMapper wmActTranMapper;
	@Override
	public WmActTran getActTranById(int id) {
		// TODO Auto-generated method stub
		return wmActTranMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<WmActTranQueryItem> getAllActTranByPage(Map map) {
		// TODO Auto-generated method stub
		return wmActTranMapper.selectAllByPage(map);
	}

	@Override
	public WmActTran saveActTran(WmActTran model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setWarehouseId(userDetails.getWarehouseId());
		model.setTranOp(userDetails.getUserId());
		model.setTranId(CodeGenerator.getCodeByCurrentTimeAndRandomNum("TRA"));
		return (WmActTran) this.save(model);
	}

	@Override
	public List<WmActTranQueryItem> selectByKey(String tranId) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmActTranMapper.selectByKey(tranId,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmActTranMapper;
	}

}