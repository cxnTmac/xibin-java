package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.fin.entity.FiCourseBalanceGLQueryItem;
import com.xibin.fin.pojo.FiCourseBalance;

public interface FiCourseBalanceService {
	public FiCourseBalance getCourseBalanceById(int id);
	
	public List<FiCourseBalance> getAllCourseBalanceByPage(Map map);
	
	public int removeCourseBalance(int id) throws BusinessException; 
	
	public FiCourseBalance saveCourseBalance (FiCourseBalance model) throws BusinessException;
	
	public List<FiCourseBalance> selectByKey(String period,String courseNo);
	
}
