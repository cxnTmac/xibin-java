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
import com.xibin.wms.dao.SysUserMapper;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryItem;
import com.xibin.wms.service.UserService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class UserServiceImpl extends BaseManagerImpl implements UserService {
	@Autowired
	private HttpSession session;
	@Resource
	private SysUserMapper userMapper;
	@Override
	public SysUser getUserById(int userId) {
		return this.userMapper.selectByPrimaryKey(userId);
	}

	@Override
	public List<SysUserQueryItem> getAllUser() {
		return this.userMapper.selectAllByPage(new HashMap<>());
	}

	@Override
	public SysUser saveUser(SysUser model) throws BusinessException{
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		List<SysUserQueryItem> list = userMapper.selectByKey(model.getUserName(),model.getCompanyId().toString());
		if(list.size()>0&&model.getId()==0){
			throw new BusinessException("用户名：["+model.getUserName()+"] 已存在，不能重复！");
		}
		return (SysUser) this.save(model);
	}
	
	@Override
	public List<SysUser> selectByUserNameAndPassword(String userName,String password) {
		// TODO Auto-generated method stub
		return this.userMapper.selectByUserNameAndPassword(userName, password);
	}

	@Override
	public List<SysUserQueryItem> getAllUserByPage(Map map) {
		// TODO Auto-generated method stub
		return this.userMapper.selectAllByPage(map);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return userMapper;
	}

	@Override
	public int removeUser(int id) {
		// TODO Auto-generated method stub
		int []ids = {id};
		return this.delete(ids);
	}
}
