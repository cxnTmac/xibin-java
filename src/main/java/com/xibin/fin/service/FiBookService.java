package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.fin.pojo.FiBook;

public interface FiBookService {
	public FiBook getBookById(int id);
	
	public List<FiBook> getAllBookByPage(Map map);
	
	public int removeBook(int id) throws BusinessException; 
	
	public FiBook saveBook (FiBook model) throws BusinessException;
	
	public List<FiBook> findByExample(FiBook example);
	
	public List<FiBook> selectByKey(Map map);

}
