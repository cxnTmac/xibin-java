package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryItem;

public interface UserService {
	public SysUser getUserById(int userId);
	
	public List<SysUserQueryItem> getAllUser();
	
	public List<SysUserQueryItem> getAllUserByPage(Map map);
	
	public int removeUser(int id);
	
	public SysUser saveUser(SysUser user) throws BusinessException;
	
	public List<SysUser> selectByUserNameAndPassword(String userName,String password);
}
