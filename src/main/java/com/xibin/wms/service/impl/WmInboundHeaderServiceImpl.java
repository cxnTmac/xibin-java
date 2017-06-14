package com.xibin.wms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.service.WmInboundHeaderService;
@Service
public class WmInboundHeaderServiceImpl  extends BaseManagerImpl implements WmInboundHeaderService {
	@Autowired
	HttpSession session;
	@Resource
	private WmInboundHeaderMapper wmInboundHeaderMapper;
	@Override
	public WmInboundHeader getInboundOrderById(int id) {
		// TODO Auto-generated method stub
		return wmInboundHeaderMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WmInboundHeader> getAllInboundOrderByPage(Map map) {
		// TODO Auto-generated method stub
		return wmInboundHeaderMapper.selectAllByPage(map);
	}

	@Override
	public List<WmInboundHeader> selectByKey(String orderNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return wmInboundHeaderMapper.selectByKey(orderNo,userDetails.getCompanyId().toString(),userDetails.getWarehouseId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return wmInboundHeaderMapper;
	}

}
