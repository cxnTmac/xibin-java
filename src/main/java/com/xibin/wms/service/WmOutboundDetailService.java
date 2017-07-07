package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.query.WmOutboundDetailQueryItem;


public interface WmOutboundDetailService {
	public WmOutboundDetail getOutboundDetailById(int userId);
	
	public List<WmOutboundDetailQueryItem> getAllOutboundDetailByPage(Map map);
	
	public List<WmOutboundDetailQueryItem> selectByKey(String orderNo,String lineNo);
	
	public List<WmOutboundDetail> selectByExample(WmOutboundDetail model);
	
	public WmOutboundDetail saveOutboundDetail (WmOutboundDetail model) throws BusinessException;
	
	public int removeOutboundDetail(int []ids,String orderNo) throws BusinessException;
	
	public Message allocByKey(String orderNo,String lineNo) throws BusinessException;
	
	public Message cancelAllocByKey(String orderNo,String lineNo) throws BusinessException;

}
