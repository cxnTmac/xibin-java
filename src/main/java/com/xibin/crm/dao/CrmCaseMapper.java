package com.xibin.crm.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.crm.pojo.CrmCase;

public interface CrmCaseMapper extends BaseMapper {
	CrmCase selectByPrimaryKey(Integer id);

	List<CrmCase> selectAllByPage(Map map);

	List<CrmCase> selectByKey(@Param("stuNum") String stuNum);

	List<CrmCase> selectByExample(CrmCase example);
}