package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.entity.InventoryUpdateEntity;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.query.WmInboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmInventoryQueryItem;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;

public interface WmToFinService {
	public Message accountInboundDetails(List<WmInboundDetailSumPriceQueryItem> details) throws BusinessException;
	
	public Message accountOutboundDetails(List<WmOutboundDetailSumPriceQueryItem> sumResults) throws BusinessException;
	
	public Message accountOutboundCost(Double sumResults) throws BusinessException;
	
}
