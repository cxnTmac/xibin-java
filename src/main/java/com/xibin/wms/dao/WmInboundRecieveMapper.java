package com.xibin.wms.dao;

import com.xibin.wms.pojo.WmInboundRecieve;

public interface WmInboundRecieveMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WmInboundRecieve record);

    int insertSelective(WmInboundRecieve record);

    WmInboundRecieve selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WmInboundRecieve record);

    int updateByPrimaryKey(WmInboundRecieve record);
}