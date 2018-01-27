package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.fin.pojo.FiVoucherDetail;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingSkuAssemble;
import com.xibin.wms.query.BdFittingSkuAssembleQueryItem;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface BdFittingSkuAssembleService {
	public BdFittingSkuAssemble getFittingSkuById(int userId);

	public List<BdFittingSkuAssembleQueryItem> getAllFittingSkuByFSkuCode(Map map);
	
	public Message saveFittingSkuAssemble (List<BdFittingSkuAssemble> details,List<BdFittingSkuAssemble> removeDetails);
	 
}
