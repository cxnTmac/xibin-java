package com.xibin.wms.dao;

import com.xibin.wms.pojo.BdZone;

public interface BdZoneMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BdZone record);

    int insertSelective(BdZone record);

    BdZone selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BdZone record);

    int updateByPrimaryKey(BdZone record);
}