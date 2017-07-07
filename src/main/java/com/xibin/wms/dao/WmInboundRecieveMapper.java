package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.query.WmInboundRecieveQueryItem;

public interface WmInboundRecieveMapper extends BaseMapper{
	List<WmInboundRecieve> selectByPrimaryKey(String ids);
    
    List<WmInboundRecieveQueryItem> selectAllByPage(Map map);
    
    List<WmInboundRecieveQueryItem> selectByKey(@Param("orderNo")String orderNo,@Param("lineNo")String lineNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmInboundRecieve> selectByExample(WmInboundRecieve example);
    
    List<Integer> selectLastRecLineNo(@Param("orderNo")String orderNo,@Param("lineNo")String lineNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
}