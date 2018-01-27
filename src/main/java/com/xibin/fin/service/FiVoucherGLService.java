package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.exception.BusinessException;
import com.xibin.fin.entity.FiCourseBalanceGLQueryItem;
import com.xibin.fin.entity.FiVoucherGLEntity;
import com.xibin.fin.entity.FiVoucherSumByCourseQueryItem;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiTerm;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.query.BdFittingSkuQueryItem;

public interface FiVoucherGLService {
	List<FiVoucherGLEntity> queryForVoucherGL(Map map);
	
	List<FiCourseBalanceGLQueryItem> queryForAccountBalance(Map map);
	
	List<FiVoucherSumByCourseQueryItem> queryForVoucherDetailSum(Map map);
	
	Integer queryForVoucherCount(Map map);
}
