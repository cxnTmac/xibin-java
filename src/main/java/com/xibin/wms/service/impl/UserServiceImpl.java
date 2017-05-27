package com.xibin.wms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.Page;
import com.xibin.wms.dao.SysUserMapper;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryCondition;
import com.xibin.wms.service.UserService;
@Service
public class UserServiceImpl implements UserService {
	@Resource
	private SysUserMapper userMapper;
	@Override
	public SysUser getUserById(int userId) {
		return this.userMapper.selectByPrimaryKey(userId);
	}

	@Override
	public List<SysUser> getAllUser() {
		return this.userMapper.selectAll();
	}
	
	@Override
	public int removeUser(int id) {
		return this.userMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int saveUser(SysUser user) {
		return this.userMapper.insert(user);
	}

	@Override
	public int updateByPrimaryKey(SysUser user) {
		// TODO Auto-generated method stub
		return this.userMapper.updateByPrimaryKey(user);
	}
	
	@Override
	public List<SysUser> selectByUserNameAndPassword(String userName,String password) {
		// TODO Auto-generated method stub
		return this.userMapper.selectByUserNameAndPassword(userName, password);
	}

	@Override
	public List<SysUser> getAllUserByPage(Map map) {
		// TODO Auto-generated method stub
		return this.userMapper.selectAllByPage(map);
	}
}
