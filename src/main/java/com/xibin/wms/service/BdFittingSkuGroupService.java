package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdFittingSkuGroup;
import com.xibin.wms.query.BdFittingSkuGroupQueryItem;

public interface BdFittingSkuGroupService {
	public BdFittingSkuGroup getSkuGroupById(int id);

	public List<BdFittingSkuGroupQueryItem> getAllSkuGropuByPage(Map map);

	public int removeSkuGroup(int id, String code) throws BusinessException;

	public BdFittingSkuGroup saveSkuGroup(BdFittingSkuGroup model) throws BusinessException;

	public List<BdFittingSkuGroup> selectByKey(String code);

	public List<BdFittingSkuGroupQueryItem> queryItemByKey(String groupCode);
}
