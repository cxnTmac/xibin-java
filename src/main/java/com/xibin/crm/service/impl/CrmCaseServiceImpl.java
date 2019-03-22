package com.xibin.crm.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.crm.dao.CrmCaseMapper;
import com.xibin.crm.pojo.CrmCase;
import com.xibin.crm.service.CrmCaseService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class CrmCaseServiceImpl extends BaseManagerImpl implements CrmCaseService {
	@Autowired
	private HttpSession session;
	@Autowired
	private CrmCaseMapper crmCaseMapper;

	@Override
	public CrmCase getCaseById(int id) {
		// TODO Auto-generated method stub
		return crmCaseMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<CrmCase> getAllCaseByPage(Map map) {
		// TODO Auto-generated method stub
		return crmCaseMapper.selectAllByPage(map);
	}

	@Override
	public int removeCase(int id, String stuNum) throws BusinessException {
		// TODO Auto-generated method stub

		int[] ids = { id };
		return this.delete(ids);
	}

	@Override
	public CrmCase saveCase(CrmCase model) throws BusinessException {
		// TODO Auto-generated method stub
		if (null == model.getId() || model.getId() == 0) {
			model.setStuNum(CodeGenerator.getCodeByCurrentTimeAndRandomNum("STU"));
		}

		return (CrmCase) this.save(model);
	}

	@Override
	public List<CrmCase> selectByKey(String stuNum) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return crmCaseMapper.selectByKey(stuNum);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return crmCaseMapper;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}

}
