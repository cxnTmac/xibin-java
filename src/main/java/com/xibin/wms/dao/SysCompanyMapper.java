package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.SysCompany;

public interface SysCompanyMapper extends BaseMapper{
	SysCompany selectByPrimaryKey(Integer id);
	    
	 List<SysCompany> selectAllByPage(Map map);
	    
	 List<SysCompany> selectByKey(@Param("companyCode")String companyCode);
	    
	 List<SysCompany> selectByExample(SysCompany example);
}