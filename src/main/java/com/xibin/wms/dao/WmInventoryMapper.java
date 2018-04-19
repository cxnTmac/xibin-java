package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.query.WmInventoryQueryItem;

public interface WmInventoryMapper extends BaseMapper {
	WmInventory selectByPrimaryKey(Integer id);

	List<WmInventoryQueryItem> selectAllByPage(Map map);

	List<WmInventoryQueryItem> selectByKey(@Param("skuCode") String orderNo, @Param("locCode") String locCode,
			@Param("lot") String lot, @Param("companyId") String companyId, @Param("warehouseId") String warehouseId);

	List<WmInventory> selectByExample(WmInventory example);

	List<WmInventory> getAvailableInvByExample(WmInventory example);

	List<WmInventoryQueryItem> getAvailableInvByPage(Map map);
}