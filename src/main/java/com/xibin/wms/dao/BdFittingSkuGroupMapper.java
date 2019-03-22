package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdFittingSkuGroup;
import com.xibin.wms.query.BdFittingSkuGroupQueryItem;

public interface BdFittingSkuGroupMapper extends BaseMapper {
	BdFittingSkuGroup selectByPrimaryKey(Integer id);

	List<BdFittingSkuGroupQueryItem> selectAllByPage(Map map);

	List<BdFittingSkuGroup> selectByKey(@Param("groupCode") String skuCode, @Param("companyId") String companyId);

	List<BdFittingSkuGroup> selectByExample(BdFittingSkuGroup example);

	List<BdFittingSkuGroupQueryItem> queryItemByKey(@Param("groupCode") String skuCode,
			@Param("companyId") String companyId);
}