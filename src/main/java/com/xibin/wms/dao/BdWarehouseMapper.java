package com.xibin.wms.dao;

import com.xibin.wms.pojo.BdWarehouse;

public interface BdWarehouseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BdWarehouse record);

    int insertSelective(BdWarehouse record);

    BdWarehouse selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BdWarehouse record);

    int updateByPrimaryKey(BdWarehouse record);
}