package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.SysCompany;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryItem;

public interface SysCompanyService {
	public SysCompany getCompanyById(int userId);
	
	public List<SysCompany> getAllCompanyByPage(Map map);
}
