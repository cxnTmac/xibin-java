package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.query.WmInventoryQueryItem;

public interface WmOutboundUpdateService {
	
	public void updateOutboundStatusByOrderNo(String orderNo) throws BusinessException;
	
	public void updateOutboundStatusByAlloc(WmOutboundAlloc alloc) throws BusinessException;
	
	public void updataOutboundStatusByOutboundDetail(String orderNo)  throws BusinessException;
	
}
