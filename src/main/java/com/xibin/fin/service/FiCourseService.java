package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.fin.pojo.FiCourse;

public interface FiCourseService {
	public FiCourse getCourseById(int id);

	public List<FiCourse> getAllCourseByPage(Map map);

	public int removeCourse(int id, String courseNo) throws BusinessException;

	public FiCourse saveCourse(FiCourse model) throws BusinessException;

	public List<FiCourse> selectByKey(String courseNo);

	public void saveCourseBalance(List<FiCourse> courses) throws BusinessException;
}
