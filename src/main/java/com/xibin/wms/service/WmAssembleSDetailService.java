package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmAssembleFDetail;
import com.xibin.wms.pojo.WmAssembleHeader;
import com.xibin.wms.pojo.WmAssembleSDetail;
import com.xibin.wms.query.WmAssembleFDetailQueryItem;
import com.xibin.wms.query.WmAssembleSDetailQueryItem;

public interface WmAssembleSDetailService {
	public WmAssembleSDetail getAssembleOrderById(int id);
	
	public List<WmAssembleSDetailQueryItem> getAllAssembleSDetailByOrderNo(Map map);
	
	public List<WmAssembleSDetail> selectByKey(String orderNo,String lineNo);
	
	public List<WmAssembleSDetail> selectByExample(WmAssembleSDetail model);
	
	public Message createAssembleSDetailByOrderNo (String orderNo) throws BusinessException;
	
	public Message cancelCreateByOrderNo(String orderNo) throws BusinessException;
	
	public Message allocByKey(String orderNo, String lineNo) throws BusinessException;
	
	public Message cancelAllocByKey(String orderNo, String lineNo) throws BusinessException;

	public WmAssembleSDetail saveAssembleSDetail(WmAssembleSDetail model) throws BusinessException;

}
