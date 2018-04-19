package com.xibin.wms.service;

import java.util.List;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.query.WmInboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;

public interface WmToFinService {
	public Message accountInboundDetails(List<WmInboundDetailSumPriceQueryItem> details, String inboundType)
			throws BusinessException;

	public Message accountOutboundDetails(List<WmOutboundDetailSumPriceQueryItem> sumResults, String outboundType)
			throws BusinessException;

	public Message accountOutboundCost(Double sumResults, String outboundType) throws BusinessException;

	public void updateInboundForRemoveVoucher(Integer voucherId);

	public void updateOutboundForRemoveVoucher(Integer voucherId);

	public void updateOutboundForRemoveCostVoucher(Integer costVoucherId);

}
