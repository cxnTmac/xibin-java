package com.xibin.wms.dao;

import com.xibin.wms.pojo.WmOutboundHeader;

public interface WmOutboundHeaderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WmOutboundHeader record);

    int insertSelective(WmOutboundHeader record);

    WmOutboundHeader selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WmOutboundHeader record);

    int updateByPrimaryKey(WmOutboundHeader record);
}