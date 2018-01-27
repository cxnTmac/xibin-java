package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmAssembleSDetail;
import com.xibin.wms.query.WmAssembleSDetailQueryItem;

public interface WmAssembleSDetailMapper extends BaseMapper{
	WmAssembleSDetail selectByPrimaryKey(Integer id);
    
    List<WmAssembleSDetailQueryItem> selectAllByOrderNo(Map map);
    
    List<WmAssembleSDetail> selectByKey(@Param("orderNo")String orderNo,@Param("lineNo")String lineNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmAssembleSDetail> selectByExample(WmAssembleSDetail example);
}
