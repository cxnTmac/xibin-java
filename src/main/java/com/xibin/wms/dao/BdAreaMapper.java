package com.xibin.wms.dao;

import com.xibin.wms.pojo.BdArea;

public interface BdAreaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BdArea record);

    int insertSelective(BdArea record);

    BdArea selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BdArea record);

    int updateByPrimaryKey(BdArea record);
}