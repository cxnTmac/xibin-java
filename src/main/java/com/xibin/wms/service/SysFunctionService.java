package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.SysCompany;
import com.xibin.wms.pojo.SysFunction;
import com.xibin.wms.pojo.SysRole;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryItem;

public interface SysFunctionService {
	public SysFunction getFunctionById(int userId);
	
	public List<SysFunction> getAllFunctionByPage(Map map);
	
	public List<SysFunction> selectByExample(SysFunction example);
	
	public List<SysFunction> selectAllMenus();
}
