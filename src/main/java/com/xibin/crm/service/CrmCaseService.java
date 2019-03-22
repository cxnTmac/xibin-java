package com.xibin.crm.service;

import java.util.List;
import java.util.Map;

import com.xibin.core.exception.BusinessException;
import com.xibin.crm.pojo.CrmCase;

public interface CrmCaseService {
	public CrmCase getCaseById(int id);

	public List<CrmCase> getAllCaseByPage(Map map);

	public int removeCase(int id, String stuNum) throws BusinessException;

	public CrmCase saveCase(CrmCase model) throws BusinessException;

	public List<CrmCase> selectByKey(String stuNum);
}
