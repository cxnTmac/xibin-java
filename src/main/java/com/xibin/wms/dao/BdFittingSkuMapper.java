package com.xibin.wms.dao;

import com.xibin.wms.pojo.BdFittingSku;

public interface BdFittingSkuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BdFittingSku record);

    int insertSelective(BdFittingSku record);

    BdFittingSku selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BdFittingSku record);

    int updateByPrimaryKey(BdFittingSku record);
}