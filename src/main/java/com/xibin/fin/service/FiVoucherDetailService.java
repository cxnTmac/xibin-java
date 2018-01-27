package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.fin.entity.FiVoucherDetailQueryItem;
import com.xibin.fin.entity.FiVoucherEntity;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;


public interface FiVoucherDetailService {
	public FiVoucherDetail getVoucherDetailById(int id);
	
	public List<FiVoucherDetail> getAllVoucherDetailByPage(Map map);
	
	public List<FiVoucherEntity> getAllVoucherEntityByPage(Map map);
	
	public int removeVoucherDetail(int id) throws BusinessException; 
	
	public int removeVoucherDetail(int []ids) throws BusinessException;
	
	public FiVoucherDetail saveVoucherDetail (FiVoucherDetail model) throws BusinessException;
	
	public List<FiVoucherDetail> selectByKey(String voucherId,String lineNo);
	
	public List<FiVoucherDetailQueryItem> selectByVoucherIdForQueryItem(String voucherId);
	
	public Message saveVoucherAndDetail(FiVoucher voucher,List<FiVoucherDetail> details,List<FiVoucherDetail> removeDetails) throws BusinessException;
}
