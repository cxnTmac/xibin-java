package com.xibin.fin.service.impl;

import java.util.ArrayList;
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
import com.xibin.fin.dao.FiCourseBalanceMapper;
import com.xibin.fin.pojo.FiCourseBalance;
import com.xibin.fin.service.FiCourseBalanceService;

@Transactional(propagation = Propagation.REQUIRED)
@Service
public class FiCourseBalanceServiceImpl extends BaseManagerImpl implements FiCourseBalanceService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiCourseBalanceMapper fiCourseBalanceMapper;

	@Override
	public FiCourseBalance getCourseBalanceById(int id) {
		// TODO Auto-generated method stub
		return fiCourseBalanceMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<FiCourseBalance> getAllCourseBalanceByPage(Map map) {
		// TODO Auto-generated method stub
		return fiCourseBalanceMapper.selectAllByPage(map);
	}

	@Override
	public List<FiCourseBalance> selectByExample(FiCourseBalance example) {
		// TODO Auto-generated method stub
		return fiCourseBalanceMapper.selectByExample(example);
	}

	@Override
	public int removeCourseBalance(int id) throws BusinessException {
		// TODO Auto-generated method stub
		int[] ids = { id };
		return this.delete(ids);
	}

	@Override
	public FiCourseBalance saveCourseBalance(FiCourseBalance model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setCompanyId(userDetails.getBookId());
		List<FiCourseBalance> list = fiCourseBalanceMapper.selectByKey(model.getPeriod(), model.getCourseNo(),
				model.getCompanyId().toString(), model.getBookId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException(
					"期间：[" + model.getCourseNo() + "] 已存在，科目：[" + model.getCourseNo() + "]余额记录已存在，不能重复！");
		}
		return (FiCourseBalance) this.save(model);
	}

	@Override
	public void updateCourseBalances(List<FiCourseBalance> courseBalances) {
		List<FiCourseBalance> saved = new ArrayList<FiCourseBalance>();
		for (FiCourseBalance e : courseBalances) {
			if (e.getId() != null) {
				saved.add(e);
			}
		}
		this.save(saved);
	}

	@Override
	public List<FiCourseBalance> selectByKey(String period, String courseNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return fiCourseBalanceMapper.selectByKey(period, courseNo, userDetails.getCompanyId().toString(),
				userDetails.getBookId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiCourseBalanceMapper;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}

}
