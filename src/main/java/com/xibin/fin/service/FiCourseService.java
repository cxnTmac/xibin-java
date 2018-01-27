package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface FiCourseService {
	public FiCourse getCourseById(int id);
	
	public List<FiCourse> getAllCourseByPage(Map map);
	
	public int removeCourse(int id,String courseNo) throws BusinessException; 
	
	public FiCourse saveCourse (FiCourse model) throws BusinessException;
	
	public List<FiCourse> selectByKey(String courseNo);
}
