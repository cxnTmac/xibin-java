package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.WmInboundDetailQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;

public interface WmInboundDetailService {
	public WmInboundDetail getInboundDetailById(int userId);
	
	public List<WmInboundDetail> getInboundDetailByIds(String []ids); 
	
	public List<WmInboundDetailQueryItem> getAllInboundDetailByPage(Map map);
	
	public List<WmInboundDetailQueryItem> selectByKey(String orderNo,String lineNo);
	
	public List<WmInboundDetail> selectByExample(WmInboundDetail model);
	
	public WmInboundDetail saveInboundDetail (WmInboundDetail model) throws BusinessException;
	
	public int removeInboundDetail(int []ids,String orderNo) throws BusinessException;
	
	public int updateStatusByKey(String orderNo,String lineNos,String status);
	
	public List<WmInboundDetailQueryItem> selectClosedOrderDetail(Map map);
}
