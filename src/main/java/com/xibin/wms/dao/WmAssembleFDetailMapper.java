package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmAssembleFDetail;
import com.xibin.wms.query.WmAssembleFDetailQueryItem;

public interface WmAssembleFDetailMapper extends BaseMapper{
	WmAssembleFDetail selectByPrimaryKey(Integer id);
    
    List<WmAssembleFDetailQueryItem> selectAllByOrderNo(Map map);
    
    List<WmAssembleFDetail> selectByKey(@Param("orderNo")String orderNo,@Param("lineNo")String lineNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmAssembleFDetail> selectByExample(WmAssembleFDetail example);
    
    List<Integer> selectLastLineNo(@Param("orderNo")String orderNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
}
