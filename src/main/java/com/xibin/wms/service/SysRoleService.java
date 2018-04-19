package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.SysCompany;
import com.xibin.wms.pojo.SysRole;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryItem;

public interface SysRoleService {
	public SysRole getRoleById(int userId);
	
	public List<SysRole> getAllRoleByPage(Map map);
}
