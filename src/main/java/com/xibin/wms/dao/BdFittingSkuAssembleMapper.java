package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingSkuAssemble;
import com.xibin.wms.query.BdFittingSkuAssembleQueryItem;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface BdFittingSkuAssembleMapper extends BaseMapper{
	BdFittingSkuAssemble selectByPrimaryKey(Integer id);
    
    List<BdFittingSkuAssembleQueryItem> selectAllByFSkuCode(Map map);
      
    List<BdFittingSkuAssemble> selectByExample(BdFittingSkuAssemble example);
}