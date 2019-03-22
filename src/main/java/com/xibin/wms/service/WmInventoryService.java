package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.query.WmInventoryQueryItem;

public interface WmInventoryService {
	public WmInventory getInventoryById(int userId);

	public List<WmInventoryQueryItem> getAllInventoryByPage(Map map);

	public List<WmInventoryQueryItem> getAvailableInvByPage(Map map);

	public List<WmInventoryQueryItem> selectByKey(String skuCode, String locCode, String lot);

	public List<WmInventory> selectByExample(WmInventory model);

	public List<WmInventory> getAvailableInvByExample(WmInventory model);

	public WmActTran updateInventory(InventoryUpdateEntity fmIn) throws BusinessException;

	public WmActTran updateInventory(InventoryUpdateEntity fmIn, InventoryUpdateEntity toIn) throws BusinessException;

	public Message move(String skuCode, String locCode, String toLoc, Double moveNum) throws BusinessException;

	public Message transfer(String skuCode, String locCode, String toSku, String toLoc, Double transferNum)
			throws BusinessException;

}
