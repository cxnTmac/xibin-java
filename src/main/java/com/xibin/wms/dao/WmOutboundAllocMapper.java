package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.query.WmOutboundAllocQueryItem;

public interface WmOutboundAllocMapper extends BaseMapper{
	WmOutboundAlloc selectByPrimaryKey(Integer id);
    
    List<WmOutboundAllocQueryItem> selectAllByPage(Map map);
    
    List<WmOutboundAllocQueryItem> selectByKey(@Param("orderNo")String orderNo,@Param("lineNo")String lineNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmOutboundAlloc> selectByExample(WmOutboundAlloc example);
    
    Double querySumCostForAccount(Map map);
}