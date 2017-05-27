package com.xibin.wms.dao;

import com.xibin.wms.pojo.WmOutboundAlloc;

public interface WmOutboundAllocMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WmOutboundAlloc record);

    int insertSelective(WmOutboundAlloc record);

    WmOutboundAlloc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WmOutboundAlloc record);

    int updateByPrimaryKey(WmOutboundAlloc record);
}