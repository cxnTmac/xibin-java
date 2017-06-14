package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface BdFittingSkuPicService {
	public BdFittingSkuPic getFittingSkuPicById(int userId);
	
	public List<BdFittingSkuPic> getAllFittingSkuPicByPage(Map map);
	
	public int removeFittingSkuPic(int idNormal,int idZip); 
	
	public BdFittingSkuPic saveFittingSkuPic (BdFittingSkuPic model);
	
	public List<BdFittingSkuPic> selectByFittingSkuCode(String fittingSkuCode);
}
