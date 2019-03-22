package com.xibin.wms.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xibin.wms.pojo.PieChartForCurrentMonthSaleByType;
import com.xibin.wms.service.WmChartsService;

@Controller
@RequestMapping(value = "/charts", produces = { "application/json;charset=UTF-8" })
public class ChartsController {
	@Resource
	private WmChartsService wmChartsService;

	@RequestMapping("/pieChartForCurrentMonthSaleByType")
	@ResponseBody
	public List<PieChartForCurrentMonthSaleByType> pieChartForCurrentMonthSaleByType(HttpServletRequest request,
			Model model) {

		return wmChartsService.pieChartForCurrentMonthSaleByType();
	}

	@RequestMapping("/pieChartAndMapForMonthSaleByProvince")
	@ResponseBody
	public List<Map<String, String>> pieChartAndMapForMonthSaleByProvince(HttpServletRequest request, Model model) {

		return wmChartsService.pieChartAndMapForMonthSaleByProvince();
	}

	@RequestMapping("/monthSaleByDate")
	@ResponseBody
	public List<Map<String, String>> monthSaleByDate(HttpServletRequest request, Model model) {

		return wmChartsService.monthSaleByDate();
	}
}
