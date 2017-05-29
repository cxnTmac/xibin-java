package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.wms.pojo.SysUser;

public interface UserService {
	public SysUser getUserById(int userId);
	
	public List<SysUser> getAllUser();
	
	public List<SysUser> getAllUserByPage(Map map);
	
	public int removeUser(int id);
	
	public int saveUser(SysUser user);
	
	public int updateByPrimaryKey(SysUser user);
	
	public List<SysUser> selectByUserNameAndPassword(String userName,String password);
}
