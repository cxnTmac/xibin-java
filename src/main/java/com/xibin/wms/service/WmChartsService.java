package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.wms.pojo.PieChartForCurrentMonthSaleByType;

public interface WmChartsService {

	public List<PieChartForCurrentMonthSaleByType> pieChartForCurrentMonthSaleByType();

	public List<Map<String, String>> pieChartAndMapForMonthSaleByProvince();

	public List<Map<String, String>> monthSaleByDate();
}
