package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.query.WmOutboundDetailPriceQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundDetailSaleHistoryQueryItem;
import com.xibin.wms.query.WmOutboundDetailSkuQueryItem;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;

public interface WmOutboundDetailMapper extends BaseMapper {
	WmOutboundDetail selectByPrimaryKey(Integer id);

	List<WmOutboundDetailQueryItem> selectClosedOrderDetail(Map map);

	List<WmOutboundDetailQueryItem> selectAllByPage(Map map);

	List<WmOutboundDetailPriceQueryItem> queryHistoryPrice(Map map);

	List<WmOutboundDetailSaleHistoryQueryItem> queryHistorySale(Map map);

	List<WmOutboundDetailQueryItem> selectByKey(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo,
			@Param("companyId") String companyId, @Param("warehouseId") String warehouseId);

	List<WmOutboundDetail> selectByExample(WmOutboundDetail example);

	List<Integer> selectLastLineNo(@Param("orderNo") String orderNo, @Param("companyId") String companyId,
			@Param("warehouseId") String warehouseId);

	List<WmOutboundDetailSumPriceQueryItem> querySumPriceGroupByBuyerForAccount(Map map);

	List<WmOutboundDetailSkuQueryItem> queryWmOutboundDetailByPage(Map map);
}