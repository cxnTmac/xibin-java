package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.entity.FiVoucherSumByCourseQueryItem;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;

public interface FiVoucherGLMapper extends BaseMapper{
	List<FiVoucherSumByCourseQueryItem> queryForVoucherGL(Map map);
	
	List<FiVoucherSumByCourseQueryItem> queryForVoucherDetailSum(Map map);
	
	Integer queryForVoucherCount(Map map);
}