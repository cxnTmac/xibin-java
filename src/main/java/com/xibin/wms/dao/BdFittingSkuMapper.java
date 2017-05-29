package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface BdFittingSkuMapper extends BaseMapper{
	BdFittingSku selectByPrimaryKey(Integer id);
    
    List<BdFittingSkuQueryItem> selectAllByPage(Map map);
    
    List<BdFittingSku> selectByKey(@Param("fittingSkuCode")String skuCode,@Param("companyId")String companyId);
    
    List<BdFittingSku> selectByExample(BdFittingSku example);
}