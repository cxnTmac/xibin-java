package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.wms.pojo.BdModel;

public interface BdModelService {
	
		public BdModel getModelById(int id);
		
		public List<BdModel> getAllModelByPage(Map map);
		
		public int removeModel(int id);
		
		public BdModel saveModel (BdModel model) throws BusinessException;
		
		public List<BdModel> selectByKey(String modelCode);

}
