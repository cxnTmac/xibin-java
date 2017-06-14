package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface WmInboundHeaderService {
	public WmInboundHeader getInboundOrderById(int userId);
	
	public List<WmInboundHeader> getAllInboundOrderByPage(Map map);
	
	
	
	public List<WmInboundHeader> selectByKey(String orderNo);
}
