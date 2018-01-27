package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiTerm;
import com.xibin.wms.pojo.BdArea;

public interface FiTermMapper extends BaseMapper{
	FiTerm selectByPrimaryKey(Integer id);
    
    List<FiTerm> selectAllByPage(Map map);
    
    List<FiTerm> selectByKey(@Param("period")String courseNo,@Param("companyId")String companyId,@Param("bookId")String bookId);
    
    List<FiTerm> selectByExample(FiTerm example);
}