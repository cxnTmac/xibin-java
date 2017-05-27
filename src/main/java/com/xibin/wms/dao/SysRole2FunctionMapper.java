package com.xibin.wms.dao;

import com.xibin.wms.pojo.SysRole2Function;

public interface SysRole2FunctionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole2Function record);

    int insertSelective(SysRole2Function record);

    SysRole2Function selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole2Function record);

    int updateByPrimaryKey(SysRole2Function record);
}