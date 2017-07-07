package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.WmInboundHeaderQueryItem;

public interface WmInboundHeaderMapper extends BaseMapper{
	WmInboundHeader selectByPrimaryKey(Integer id);
    
    List<WmInboundHeaderQueryItem> selectAllByPage(Map map);
    
    List<WmInboundHeaderQueryItem> selectByKey(@Param("orderNo")String orderNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmInboundHeader> selectByExample(WmInboundHeader example);
}