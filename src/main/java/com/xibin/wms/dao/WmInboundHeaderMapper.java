package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmInboundHeader;

public interface WmInboundHeaderMapper extends BaseMapper{
	WmInboundHeader selectByPrimaryKey(Integer id);
    
    List<WmInboundHeader> selectAllByPage(Map map);
    
    List<WmInboundHeader> selectByKey(@Param("orderNo")String orderNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmInboundHeader> selectByExample(WmInboundHeader example);
}