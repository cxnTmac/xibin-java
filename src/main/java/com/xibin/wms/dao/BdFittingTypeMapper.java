package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdFittingType;

public interface BdFittingTypeMapper extends BaseMapper{

    BdFittingType selectByPrimaryKey(Integer id);
    
    List<BdFittingType> selectAllByPage(Map map);
    
    List<BdFittingType> selectByKey(@Param("fittingTypeCode")String fittingTypeCode,@Param("companyId")int companyId);
}