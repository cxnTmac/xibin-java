package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.fin.pojo.FiVoucher;


public interface FiVoucherService {
	public FiVoucher getVoucherById(int id);
	
	public List<FiVoucher> getAllVoucherByPage(Map map);
	
	public int removeVoucher(int id) throws BusinessException; 
	
	public FiVoucher saveVoucher (FiVoucher model) throws BusinessException;
	
	public List<FiVoucher> selectByKey(String voucherNum,String voucherWord,String period);
	
	public Integer getMaxVoucherNum(String voucherWord,String period);
	
	public Message checkVoucher(FiVoucher voucher) throws BusinessException;
	
	public Message cancelCheckVoucher(FiVoucher voucher) throws BusinessException;
}
