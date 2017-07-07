package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface BdAreaService {
	public BdArea getAreaById(int id);
	
	public List<BdArea> getAllAreaByPage(Map map);
	
	public int removeArea(int id,String areaCode) throws BusinessException; 
	
	public BdArea saveArea (BdArea model) throws BusinessException;
	
	public List<BdArea> selectByKey(String areaCode);
}
