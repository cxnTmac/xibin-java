package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmAssembleAlloc;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.query.WmInventoryQueryItem;

public interface WmAssembleUpdateService {
	
	public void updateAssembleStatusByOrderNo(String orderNo) throws BusinessException;
	
	public void updateAssembleStatusByAlloc(WmAssembleAlloc alloc) throws BusinessException;
	
	public void updataAssembleStatusByAssembleSDetail(String orderNo)  throws BusinessException;
	
}
