package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.WmSkuCost;
import com.xibin.wms.query.WmSkuCostQueryItem;

public interface WmSkuCostMapper extends BaseMapper{
	WmSkuCost selectByPrimaryKey(Integer id);
    
    List<WmSkuCostQueryItem> selectAllByPage(Map map);
    
    List<WmSkuCost> selectByExample(BdArea example);
    
    WmSkuCost selectLastCostBySkuCode(@Param("fittingSkuCode")String fittingSkuCode,@Param("companyId")String companyId);
}