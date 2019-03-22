package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.BdSkuAnotherName;

public interface BdSkuAnotherNameService {
	public BdSkuAnotherName getAnotherNameById(int id);

	public List<BdSkuAnotherName> getAllAnotherNameByPage(Map map);

	public int removeAnotherName(int id, String fittingSkuCode, String customerCode) throws BusinessException;

	public BdSkuAnotherName saveAnotherName(BdSkuAnotherName model) throws BusinessException;

	public List<BdSkuAnotherName> selectByKey(String fittingSkuCode, String customerCode);

	public Message importByExcel(MultipartFile file, String customerCode, String skuCodeColumnName)
			throws BusinessException;

	public Message importByExcelForSave(MultipartFile file, String customerCode, String skuCodeColumnName,
			String customerskuCodeColumnName, String remark) throws BusinessException;

	public List<BdSkuAnotherName> selectByExample(BdSkuAnotherName model);
}
