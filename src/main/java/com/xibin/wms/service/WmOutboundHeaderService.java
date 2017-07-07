package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;

public interface WmOutboundHeaderService {
	public WmOutboundHeader getOutboundOrderById(int userId);
	
	public List<WmOutboundHeaderQueryItem> getAllOutboundOrderByPage(Map map);
	
	public List<WmOutboundHeaderQueryItem> selectByKey(String orderNo);
	
	public List<WmOutboundHeader> selectByExample(WmOutboundHeader model);
	
	public WmOutboundHeaderQueryItem saveOutbound (WmOutboundHeader model) throws BusinessException;
	
	public int remove(String orderNo) throws BusinessException;
	
	public WmOutboundHeaderQueryItem audit(String orderNo) throws BusinessException;
	
	public WmOutboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException;
}
