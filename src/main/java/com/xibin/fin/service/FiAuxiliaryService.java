package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.fin.pojo.FiAuxiliary;


public interface FiAuxiliaryService {
	public FiAuxiliary getAuxiliaryById(int id);
	
	public List<FiAuxiliary> getAllAuxiliaryByPage(Map map);
	
	public int removeAuxiliary(int id) throws BusinessException;
	
	public FiAuxiliary saveAuxiliary (FiAuxiliary model) throws BusinessException;
	
}
