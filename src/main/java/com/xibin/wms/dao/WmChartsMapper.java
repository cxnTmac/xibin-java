package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.PieChartForCurrentMonthSaleByType;

public interface WmChartsMapper extends BaseMapper {

	List<PieChartForCurrentMonthSaleByType> pieChartForCurrentMonthSaleByType(Map map);

	List<Map<String, String>> pieChartAndMapForMonthSaleByProvince(Map map);

	List<Map<String, String>> monthSaleByDate(Map map);
}