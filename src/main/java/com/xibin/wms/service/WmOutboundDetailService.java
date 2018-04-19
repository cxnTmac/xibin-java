package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.query.WmOutboundDetailPriceQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundDetailSaleHistoryQueryItem;

public interface WmOutboundDetailService {
	public WmOutboundDetail getOutboundDetailById(int userId);

	public List<WmOutboundDetail> getInboundDetailByIds(String[] ids);

	public List<WmOutboundDetailQueryItem> getAllOutboundDetailByPage(Map map);

	public List<WmOutboundDetailQueryItem> selectByKey(String orderNo, String lineNo);

	public List<WmOutboundDetail> selectByExample(WmOutboundDetail model);

	public WmOutboundDetailQueryItem saveOutboundDetail(WmOutboundDetail model) throws BusinessException;

	public WmOutboundDetailQueryItem saveOutboundDetailWithOutCheck(WmOutboundDetail model) throws BusinessException;

	public int removeOutboundDetail(int[] ids, String orderNo) throws BusinessException;

	public Message allocByKey(String orderNo, String lineNo) throws BusinessException;

	public Message cancelAllocByKey(String orderNo, String lineNo) throws BusinessException;

	public List<WmOutboundDetailPriceQueryItem> queryHistoryPrice(Map map);

	public List<WmOutboundDetailSaleHistoryQueryItem> queryHistorySale(Map map);

	public List<WmOutboundDetailQueryItem> selectClosedOrderDetail(Map map);

}
