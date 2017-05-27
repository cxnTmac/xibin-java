package com.xibin.wms.dao;

import com.xibin.wms.pojo.SysCompany;

public interface SysCompanyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysCompany record);

    int insertSelective(SysCompany record);

    SysCompany selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysCompany record);

    int updateByPrimaryKey(SysCompany record);
}