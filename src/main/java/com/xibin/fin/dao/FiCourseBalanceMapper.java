package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.entity.FiCourseBalanceBSQueryItem;
import com.xibin.fin.entity.FiCourseBalanceForwardQueryItem;
import com.xibin.fin.entity.FiCourseBalanceGLQueryItem;
import com.xibin.fin.pojo.FiCourseBalance;

public interface FiCourseBalanceMapper extends BaseMapper {
	FiCourseBalance selectByPrimaryKey(Integer id);

	List<FiCourseBalance> selectAllByPage(Map map);

	List<FiCourseBalance> selectByKey(@Param("period") String period, @Param("courseNo") String courseNo,
			@Param("companyId") String companyId, @Param("bookId") String bookId);

	List<FiCourseBalance> selectByExample(FiCourseBalance example);

	List<FiCourseBalanceGLQueryItem> queryCourseBalanceForVoucherGL(Map map);

	List<FiCourseBalanceForwardQueryItem> queryCourseBalanceForward(Map map);

	List<FiCourseBalanceBSQueryItem> queryForBalanceSheet(Map map);
}