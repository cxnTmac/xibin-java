package com.xibin.wms.dao;

import com.xibin.wms.pojo.WmOutboundDetail;

public interface WmOutboundDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WmOutboundDetail record);

    int insertSelective(WmOutboundDetail record);

    WmOutboundDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WmOutboundDetail record);

    int updateByPrimaryKey(WmOutboundDetail record);
}