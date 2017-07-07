package com.xibin.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.wms.pojo.WmActTran;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInventory;
import com.xibin.wms.query.WmActTranQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmInventoryQueryItem;

public interface WmActTranMapper extends BaseMapper{
	WmActTran selectByPrimaryKey(Integer id);
    
    List<WmActTranQueryItem> selectAllByPage(Map map);
    
    List<WmActTranQueryItem> selectByKey(@Param("tranId")String tranId,@Param("companyId")String companyId,@Param("warehouseId")String warehouseId);
    
    List<WmActTran> selectByExample(WmActTran example);
}