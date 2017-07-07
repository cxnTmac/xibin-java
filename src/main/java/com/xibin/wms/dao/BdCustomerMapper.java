package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.BdCustomer;
import com.xibin.wms.pojo.BdLoc;
import com.xibin.wms.query.BdCustomerQueryItem;
import com.xibin.wms.query.BdLocQueryItem;

public interface BdCustomerMapper extends BaseMapper{
	BdCustomer selectByPrimaryKey(Integer id);
    
    List<BdCustomerQueryItem> selectAllByPage(Map map);
    
    List<BdCustomerQueryItem> selectByKey(@Param("customerCode")String customerCode,@Param("companyId")String companyId);
    
    List<BdCustomer> selectByExample(BdCustomer example);
}