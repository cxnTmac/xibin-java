package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdCustomer;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.query.BdCustomerQueryItem;
import com.xibin.wms.query.BdZoneQueryItem;

public interface BdCustomerService {
	public BdCustomer getCustomerById(int id);
	
	public List<BdCustomerQueryItem> getAllCustomerByPage(Map map);
	
	public int removeCustomer (int id,String zoneCode) throws BusinessException; 
	
	public BdCustomer saveCustomer (BdCustomer model) throws BusinessException;
	
	public List<BdCustomerQueryItem> selectByKey(String customerCode);
}
