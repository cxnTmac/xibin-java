package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmAssembleFDetail;
import com.xibin.wms.pojo.WmAssembleHeader;
import com.xibin.wms.query.WmAssembleFDetailQueryItem;

public interface WmAssembleFDetailService {
	public WmAssembleFDetail getAssembleOrderById(int id);
	
	public List<WmAssembleFDetailQueryItem> getAllAssembleFDetailByOrderNo(Map map);
	
	public List<WmAssembleFDetail> selectByKey(String orderNo,String lineNo);
	
	public List<WmAssembleFDetail> selectByExample(WmAssembleFDetail model);
	
	public WmAssembleFDetail saveAssembleFDetail (WmAssembleFDetail model) throws BusinessException;
	
	public int remove(String orderNo,String lineNo) throws BusinessException;
	
	public Message assemble(String orderNo,String lineNo,double assembleNum) throws BusinessException;

}
