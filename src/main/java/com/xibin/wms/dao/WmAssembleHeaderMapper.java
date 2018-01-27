package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmAssembleHeader;

public interface WmAssembleHeaderMapper extends BaseMapper{
	WmAssembleHeader selectByPrimaryKey(Integer id);
    
    List<WmAssembleHeader> selectAllByPage(Map map);
    
    List<WmAssembleHeader> selectByKey(@Param("orderNo")String orderNo,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmAssembleHeader> selectByExample(WmAssembleHeader example);
}
