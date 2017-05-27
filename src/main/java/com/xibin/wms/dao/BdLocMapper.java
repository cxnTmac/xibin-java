package com.xibin.wms.dao;

import com.xibin.wms.pojo.BdLoc;

public interface BdLocMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BdLoc record);

    int insertSelective(BdLoc record);

    BdLoc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BdLoc record);

    int updateByPrimaryKey(BdLoc record);
}