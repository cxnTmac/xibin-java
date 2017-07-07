package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdArea;

public interface BdAreaMapper extends BaseMapper{
	BdArea selectByPrimaryKey(Integer id);
    
    List<BdArea> selectAllByPage(Map map);
    
    List<BdArea> selectByKey(@Param("areaCode")String skuCode,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<BdArea> selectByExample(BdArea example);
}