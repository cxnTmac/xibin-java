package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;



import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.pojo.FiAuxiliary;

public interface FiAuxiliaryMapper extends BaseMapper{
	FiAuxiliary selectByPrimaryKey(Integer id);
    
    List<FiAuxiliary> selectAllByPage(Map map);
    
    List<FiAuxiliary> selectByExample(FiAuxiliary example);
}