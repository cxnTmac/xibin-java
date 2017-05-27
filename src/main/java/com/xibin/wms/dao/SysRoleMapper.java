package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.pojo.SysRole;

public interface SysRoleMapper extends BaseMapper{
	SysRole selectByPrimaryKey(Integer id);
    
    List<SysRole> selectAllByPage(Map map);
    
    List<SysRole> selectByKey(@Param("roleCode")String roleCode,@Param("companyId")int companyId);
}