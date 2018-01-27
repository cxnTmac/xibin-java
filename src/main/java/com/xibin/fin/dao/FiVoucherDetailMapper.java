package com.xibin.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xibin.core.daosupport.BaseMapper;
import com.xibin.fin.entity.FiVoucherDetailQueryItem;
import com.xibin.fin.entity.FiVoucherDetailSumQueryItem;
import com.xibin.fin.entity.FiVoucherEntity;
import com.xibin.fin.entity.FiVoucherSumByCourseQueryItem;
import com.xibin.fin.pojo.FiVoucherDetail;

public interface FiVoucherDetailMapper extends BaseMapper{
	FiVoucherDetail selectByPrimaryKey(Integer id);
    
    List<FiVoucherDetail> selectAllByPage(Map map);
    
    List<FiVoucherEntity> selectAllEntityByPage(Map map);
    
    List<FiVoucherDetail> selectByKey(@Param("voucherId")String voucherId,@Param("lineNo")String lineNo,@Param("companyId")String companyId,@Param("bookId")String bookId);
    
    List<FiVoucherDetailQueryItem> selectByVoucherIdForQueryItem(@Param("voucherId")String voucherId);
    
    List<FiVoucherDetailSumQueryItem> selectByVoucherIdForSum(@Param("voucherId")String voucherId);
    
    List<FiVoucherDetail> selectByExample(FiVoucherDetail example);
}