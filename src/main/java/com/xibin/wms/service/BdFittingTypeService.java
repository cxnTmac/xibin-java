package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.pojo.SysUser;

public interface BdFittingTypeService {
	public BdFittingType getFittingTypeById(int userId);
	
	public List<BdFittingType> getAllFittingTypeByPage(Map map);
	
	public int removeFittingType(int id);
	
	public BdFittingType saveFittingType (BdFittingType model) throws BusinessException;
	
	public List<BdFittingType> selectByKey(String fittingTypeCode);
}
