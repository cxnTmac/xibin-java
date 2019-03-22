package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface BdFittingSkuService {
	public BdFittingSku getFittingSkuById(int userId);

	public List<BdFittingSkuQueryItem> MgetAllFittingSkuByPageWithOnePic(Map map);

	public List<BdFittingSkuQueryItem> getAllFittingSkuByPage(Map map);

	public int removeFittingSku(int id, String fittingTypeCode) throws BusinessException;

	public BdFittingSku saveFittingSku(BdFittingSku model) throws BusinessException;

	public List<BdFittingSkuQueryItem> selectByKey(String fittingTypeCode);

	public List<BdFittingSkuQueryItem> selectByKey(String skuCode, String companyId);

	public Message importByExcel(MultipartFile file, String skuCodeColumnName) throws BusinessException;

	public List<BdFittingSku> selectByExample(BdFittingSku model);
}
