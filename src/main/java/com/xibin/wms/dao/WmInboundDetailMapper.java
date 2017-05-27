package com.xibin.wms.dao;

import com.xibin.wms.pojo.WmInboundDetail;

public interface WmInboundDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WmInboundDetail record);

    int insertSelective(WmInboundDetail record);

    WmInboundDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WmInboundDetail record);

    int updateByPrimaryKey(WmInboundDetail record);
}