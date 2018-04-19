package com.xibin.wms.service.impl;

import java.util.HashMap;
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
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.dao.SysCompanyMapper;
import com.xibin.wms.dao.SysUserMapper;
import com.xibin.wms.pojo.SysCompany;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryItem;
import com.xibin.wms.service.SysCompanyService;
import com.xibin.wms.service.UserService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class SysCompanyServiceImpl extends BaseManagerImpl implements SysCompanyService {
	@Autowired
	private HttpSession session;
	@Resource
	private SysCompanyMapper sysCompanyMapper;
	@Override
	public SysCompany getCompanyById(int userId) {
		return this.sysCompanyMapper.selectByPrimaryKey(userId);
	}


	@Override
	public List<SysCompany> getAllCompanyByPage(Map map) {
		// TODO Auto-generated method stub
		return this.sysCompanyMapper.selectAllByPage(map);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return sysCompanyMapper;
	}


}
