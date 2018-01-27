package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;


import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.pojo.FiBook;

public interface FiBookMapper extends BaseMapper{
	FiBook selectByPrimaryKey(Integer id);
    
    List<FiBook> selectAllByPage(Map map);
    
    List<FiBook> selectByExample(FiBook example);
    
    List<FiBook> selectByKey(Map map);
}