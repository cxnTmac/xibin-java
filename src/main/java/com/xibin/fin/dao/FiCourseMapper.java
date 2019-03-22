package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.pojo.FiCourse;

public interface FiCourseMapper extends BaseMapper {
	FiCourse selectByPrimaryKey(Integer id);

	List<FiCourse> selectAllByPage(Map map);

	List<FiCourse> selectAllByCondition(Map map);

	List<FiCourse> selectByKey(@Param("courseNo") String courseNo, @Param("companyId") String companyId,
			@Param("bookId") String bookId);

	List<FiCourse> selectByExample(FiCourse example);

}