package com.xibin.wms.dao;

import com.xibin.wms.pojo.WmInboundHeader;

public interface WmInboundHeaderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WmInboundHeader record);

    int insertSelective(WmInboundHeader record);

    WmInboundHeader selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WmInboundHeader record);

    int updateByPrimaryKey(WmInboundHeader record);
}