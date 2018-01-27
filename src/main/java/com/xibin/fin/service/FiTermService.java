package com.xibin.fin.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiTerm;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface FiTermService {
	public FiTerm getTermById(int id);
	
	public List<FiTerm> getAllTermByPage(Map map);
	
	public int removeTerm(int id,String period) throws BusinessException; 
	
	public FiTerm saveTerm (FiTerm model) throws BusinessException;
	
	public List<FiTerm> selectByKey(String period);
	
	public FiTerm getCurrentTerm(Integer companyId);
	
	public Message lossAndGainBroughtForward(Date date,String summary,String voucherWord) throws BusinessException;
	
	public Message endTerm() throws BusinessException;
}
