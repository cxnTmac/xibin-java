package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmAssembleAlloc;
import com.xibin.wms.query.WmAssembleAllocQueryItem;

public interface WmAssembleAllocMapper extends BaseMapper{
	WmAssembleAlloc selectByPrimaryKey(Integer id);
    
    List<WmAssembleAllocQueryItem> selectAllByOrderNo(Map map);
    
    List<WmAssembleAlloc> selectByKey(@Param("allocId")String allocId,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmAssembleAlloc> selectByExample(WmAssembleAlloc example);
}
