package com.xibin.wms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.dao.WmChartsMapper;
import com.xibin.wms.pojo.PieChartForCurrentMonthSaleByType;
import com.xibin.wms.service.WmChartsService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmChartsServiceImpl extends BaseManagerImpl implements WmChartsService {
	@Autowired
	private HttpSession session;
	@Autowired
	private WmChartsMapper wmChartsMapper;

	@Override
	public List<PieChartForCurrentMonthSaleByType> pieChartForCurrentMonthSaleByType() {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map<String, String> map = new HashMap<String, String>();
		map.put("companyId", userDetails.getCompanyId().toString());
		map.put("warehouseId", userDetails.getWarehouseId().toString());
		return wmChartsMapper.pieChartForCurrentMonthSaleByType(map);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> pieChartAndMapForMonthSaleByProvince() {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map<String, String> map = new HashMap<String, String>();
		map.put("companyId", userDetails.getCompanyId().toString());
		map.put("warehouseId", userDetails.getWarehouseId().toString());
		return wmChartsMapper.pieChartAndMapForMonthSaleByProvince(map);
	}

	@Override
	public List<Map<String, String>> monthSaleByDate() {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map<String, String> map = new HashMap<String, String>();
		map.put("companyId", userDetails.getCompanyId().toString());
		map.put("warehouseId", userDetails.getWarehouseId().toString());
		return wmChartsMapper.monthSaleByDate(map);
	}

}
