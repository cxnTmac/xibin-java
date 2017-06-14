package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface BdFittingSkuPicMapper extends BaseMapper{
	BdFittingSkuPic selectByPrimaryKey(Integer id);
    
    List<BdFittingSkuPic> selectAllByPage(Map map);
    
    List<BdFittingSkuPic> selectByFittingSkuCode(@Param("fittingSkuCode")String skuCode,@Param("companyId")String companyId);
    
    List<BdFittingSkuPic> selectByExample(BdFittingSkuPic example);
}