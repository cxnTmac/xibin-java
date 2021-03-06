package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.WmInboundHeaderQueryItem;

public interface WmInboundHeaderService {
	public WmInboundHeader getInboundOrderById(int userId);

	public List<WmInboundHeaderQueryItem> getAllInboundOrderByPage(Map map);

	public List<WmInboundHeaderQueryItem> selectByKey(String orderNo);

	public List<WmInboundHeader> selectByExample(WmInboundHeader model);

	public WmInboundHeaderQueryItem saveInbound(WmInboundHeader model) throws BusinessException;

	public int remove(String orderNo) throws BusinessException;

	public WmInboundHeaderQueryItem audit(String orderNo) throws BusinessException;

	public WmInboundHeaderQueryItem close(String orderNo) throws BusinessException;

	public WmInboundHeaderQueryItem cancelAudit(String orderNo) throws BusinessException;

	public Message accountByOrderNo(String orderNo) throws BusinessException;

	public Message accountByOrderNos(List<String> orderNos, String inboundType) throws BusinessException;

	public Message accountForCostByOrderNo(String orderNo) throws BusinessException;

	public Message accountForCostByOrderNos(List<String> orderNos, String inboundType) throws BusinessException;
}
