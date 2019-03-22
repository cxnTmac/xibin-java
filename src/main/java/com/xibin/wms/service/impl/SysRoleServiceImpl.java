package com.xibin.wms.service.impl;

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
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.dao.SysRoleMapper;
import com.xibin.wms.pojo.SysRole;
import com.xibin.wms.service.SysRoleService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class SysRoleServiceImpl extends BaseManagerImpl implements SysRoleService {
	@Autowired
	private HttpSession session;
	@Resource
	private SysRoleMapper sysRoleMapper;

	@Override
	public SysRole getRoleById(int userId) {
		return this.sysRoleMapper.selectByPrimaryKey(userId);
	}

	@Override
	public List<SysRole> getAllRoleByPage(Map map) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId());
		return this.sysRoleMapper.selectAllByPage(map);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return sysRoleMapper;
	}

}
