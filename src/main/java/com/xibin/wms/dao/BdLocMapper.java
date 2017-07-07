package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdLoc;
import com.xibin.wms.query.BdLocQueryItem;

public interface BdLocMapper extends BaseMapper{
	BdLoc selectByPrimaryKey(Integer id);
    
    List<BdLocQueryItem> selectAllByPage(Map map);
    
    List<BdLocQueryItem> selectByKey(@Param("locCode")String locCode,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<BdLoc> selectByExample(BdLoc example);
}