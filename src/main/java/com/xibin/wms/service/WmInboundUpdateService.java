package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.query.WmInventoryQueryItem;

public interface WmInboundUpdateService {
	public void updataInboundStatusByInboundReceive(String orderNo)  throws BusinessException;
	
}
