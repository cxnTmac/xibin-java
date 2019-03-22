package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.pojo.FiCourseAuxiliaryBalance;

public interface FiCourseAuxiliaryBalanceMapper extends BaseMapper {
	FiCourseAuxiliaryBalance selectByPrimaryKey(Integer id);

	List<FiCourseAuxiliaryBalance> selectAllByPage(Map map);

	List<FiCourseAuxiliaryBalance> selectByKey(@Param("faCode") String faCode, @Param("period") String period,
			@Param("courseNo") String courseNo, @Param("companyId") String companyId, @Param("bookId") String bookId);

	List<FiCourseAuxiliaryBalance> selectByExample(FiCourseAuxiliaryBalance example);
}