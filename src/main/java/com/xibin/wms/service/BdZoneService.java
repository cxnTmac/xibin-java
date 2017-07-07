package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.query.BdZoneQueryItem;

public interface BdZoneService {
	public BdZone getZoneById(int id);
	
	public List<BdZoneQueryItem> getAllZoneByPage(Map map);
	
	public int removeZone(int id,String zoneCode) throws BusinessException; 
	
	public BdZone saveZone (BdZone model) throws BusinessException;
	
	public List<BdZoneQueryItem> selectByKey(String zoneCode);
}
