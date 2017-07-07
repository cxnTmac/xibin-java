package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdLoc;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.query.BdLocQueryItem;
import com.xibin.wms.query.BdZoneQueryItem;

public interface BdLocService {
	public BdLoc getLocById(int id);
	
	public List<BdLocQueryItem> getAllLocByPage(Map map);
	
	public int removeLoc(int id,String locCode) throws BusinessException; 
	
	public BdLoc saveLoc (BdLoc model) throws BusinessException;
	
	public List<BdLocQueryItem> selectByKey(String locCode);
}
