package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.query.WmActTranQueryItem;

public interface WmActTranService {
	public WmActTran getActTranById(int id);
	
	public List<WmActTranQueryItem> getAllActTranByPage(Map map);
	
	public WmActTran saveActTran (WmActTran model) throws BusinessException;
	
	public List<WmActTranQueryItem> selectByKey(String tranId);
}
