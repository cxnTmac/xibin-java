package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdModel;

public interface BdModelMapper extends BaseMapper{
	BdModel selectByPrimaryKey(Integer id);
    
    List<BdModel> selectAllByPage(Map map);
    
    List<BdModel> selectByKey(@Param("modelCode")String modelCode);
}