package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;

public interface WmOutboundHeaderMapper extends BaseMapper{
	WmOutboundHeader selectByPrimaryKey(Integer id);
    
    List<WmOutboundHeaderQueryItem> selectAllByPage(Map map);
    
    List<WmOutboundHeaderQueryItem> selectByKey(@Param("orderNo")String orderNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmOutboundHeader> selectByExample(WmOutboundHeader example);
}