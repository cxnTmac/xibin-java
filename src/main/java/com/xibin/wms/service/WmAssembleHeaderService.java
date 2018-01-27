package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.WmAssembleHeader;

public interface WmAssembleHeaderService {
	public WmAssembleHeader getAssembleOrderById(int id);
	
	public List<WmAssembleHeader> getAllAssembleOrderByPage(Map map);
	
	public List<WmAssembleHeader> selectByKey(String orderNo);
	
	public List<WmAssembleHeader> selectByExample(WmAssembleHeader model);
	
	public WmAssembleHeader saveAssembleOrder (WmAssembleHeader model) throws BusinessException;
	
	public int remove(String orderNo) throws BusinessException;
	
	public WmAssembleHeader audit(String orderNo) throws BusinessException;
	
	public WmAssembleHeader cancelAudit(String orderNo) throws BusinessException;

}
