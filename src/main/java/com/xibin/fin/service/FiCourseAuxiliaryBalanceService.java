package com.xibin.fin.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.fin.pojo.FiCourseAuxiliaryBalance;

public interface FiCourseAuxiliaryBalanceService {
	public FiCourseAuxiliaryBalance getCourseAuxiliaryBalanceById(int id);

	public List<FiCourseAuxiliaryBalance> getAllCourseAuxiliaryBalanceByPage(Map map);

	public int removeCourseAuxiliaryBalance(int id) throws BusinessException;

	public FiCourseAuxiliaryBalance saveCourseAuxiliaryBalance(FiCourseAuxiliaryBalance model);

	public List<FiCourseAuxiliaryBalance> selectByKey(String faCode, String period, String courseNo);

	public Message addCustomerCourseAuxiliaryBlance(String customerCode, double balance, String courseNo);
}
