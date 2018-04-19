package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.SysFunction;

public interface SysFunctionMapper extends BaseMapper{
	SysFunction selectByPrimaryKey(Integer id);
    
    List<SysFunction> selectAllByPage(Map map);
    
    List<SysFunction> selectByKey(@Param("functionCode")String functionCode);
    
    List<SysFunction> selectByExample(SysFunction example);
}