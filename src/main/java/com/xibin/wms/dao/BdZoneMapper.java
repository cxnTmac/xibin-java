package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.query.BdZoneQueryItem;

public interface BdZoneMapper extends BaseMapper{
	BdZone selectByPrimaryKey(Integer id);
    
    List<BdZoneQueryItem> selectAllByPage(Map map);
    
    List<BdZoneQueryItem> selectByKey(@Param("zoneCode")String zoneCode,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<BdZone> selectByExample(BdZone example);
}