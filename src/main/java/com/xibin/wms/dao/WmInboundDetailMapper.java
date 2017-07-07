package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.query.WmInboundDetailQueryItem;

public interface WmInboundDetailMapper extends BaseMapper{
	WmInboundDetail selectByPrimaryKey(Integer id);
	
	int updateStatusByKey(@Param("orderNo")String orderNo,@Param("lineNos")String lineNos,@Param("status")String status,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmInboundDetailQueryItem> selectAllByPage(Map map);
    
    List<WmInboundDetailQueryItem> selectByKey(@Param("orderNo")String orderNo,@Param("lineNo")String lineNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmInboundDetail> selectByExample(WmInboundDetail example);
    
    List<Integer> selectLastLineNo(@Param("orderNo")String orderNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
}