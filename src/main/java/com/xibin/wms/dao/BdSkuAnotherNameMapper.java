package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdSkuAnotherName;

public interface BdSkuAnotherNameMapper extends BaseMapper {
	BdSkuAnotherName selectByPrimaryKey(Integer id);

	List<BdSkuAnotherName> selectAllByPage(Map map);

	List<BdSkuAnotherName> selectByKey(@Param("fittingSkuCode") String fittingSkuCode,
			@Param("customerCode") String customerCode, @Param("companyId") String companyId);

	List<BdSkuAnotherName> selectByExample(BdSkuAnotherName example);
}