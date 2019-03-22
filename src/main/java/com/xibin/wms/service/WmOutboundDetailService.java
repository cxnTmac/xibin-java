package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.query.WmOutboundDetailPriceQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundDetailSaleHistoryQueryItem;
import com.xibin.wms.query.WmOutboundDetailSkuQueryItem;

public interface WmOutboundDetailService {
	public WmOutboundDetail getOutboundDetailById(int userId);

	public List<WmOutboundDetail> getInboundDetailByIds(String[] ids);

	public List<WmOutboundDetailQueryItem> getAllOutboundDetailByPage(Map map);

	public List<WmOutboundDetailQueryItem> selectByKey(String orderNo, String lineNo);

	public List<WmOutboundDetail> selectByExample(WmOutboundDetail model);

	public WmOutboundDetailQueryItem saveOutboundDetail(WmOutboundDetail model) throws BusinessException;

	public WmOutboundDetailQueryItem saveOutboundDetailWithOutCheck(WmOutboundDetail model) throws BusinessException;

	public Message removeOutboundDetail(int[] ids, String orderNo) throws BusinessException;

	public Message removeOutboundDetailAndCreateNewOrder(int[] ids, String orderNo) throws BusinessException;

	public Message allocByKey(String orderNo, String lineNo, String type) throws BusinessException;

	public Message cancelAllocByKey(String orderNo, String lineNo) throws BusinessException;

	public List<WmOutboundDetailPriceQueryItem> queryHistoryPrice(Map map);

	public List<WmOutboundDetailSaleHistoryQueryItem> queryHistorySale(Map map);

	public List<WmOutboundDetailQueryItem> selectClosedOrderDetail(Map map);

	public Message allocByOrderNo(String orderNo);

	public Message cancelAllocByOrderNo(String orderNo);

	public List<WmOutboundDetailSkuQueryItem> queryWmOutboundDetailByPage(Map map);

	public Message importByExcel(MultipartFile file, String orderNo, String loc, String skuCodeColumnName,
			String priceColumnName, String numColumnName) throws BusinessException;

}
