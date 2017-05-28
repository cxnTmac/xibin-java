package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdFittingSku;

public interface BdFittingSkuMapper extends BaseMapper{
	BdFittingSku selectByPrimaryKey(Integer id);
    
    List<BdFittingSku> selectAllByPage(Map map);
    
    List<BdFittingSku> selectByKey(@Param("skuCode")String skuCode,@Param("companyId")String companyId);
    
    List<BdFittingSku> selectByExample(BdFittingSku example);
}