package com.xibin.fin.service.impl;

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
import com.xibin.fin.dao.FiCourseMapper;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiCourseBalance;
import com.xibin.fin.service.FiCourseBalanceService;
import com.xibin.fin.service.FiCourseService;

@Transactional(propagation = Propagation.REQUIRED)
@Service
public class FiCourseServiceImpl extends BaseManagerImpl implements FiCourseService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiCourseMapper fiCourseMapper;
	@Autowired
	private FiCourseBalanceService fiCourseBalanceService;

	@Override
	public FiCourse getCourseById(int id) {
		// TODO Auto-generated method stub
		return fiCourseMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<FiCourse> getAllCourseByPage(Map map) {
		// TODO Auto-generated method stub
		return fiCourseMapper.selectAllByPage(map);
	}

	@Override
	public int removeCourse(int id, String courseNo) throws BusinessException {
		// TODO Auto-generated method stub
		int[] ids = { id };
		return this.delete(ids);
	}

	@Override
	public FiCourse saveCourse(FiCourse model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		List<FiCourse> list = fiCourseMapper.selectByKey(model.getCourseNo(), model.getCompanyId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getCourseNo() + "] 已存在，不能重复！");
		}
		return (FiCourse) this.save(model);
	}

	@Override
	public void saveCourseBalance(List<FiCourse> courses) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		String beginPeriod = userDetails.getBeginYear() + userDetails.getPeriod();
		if (!beginPeriod.equals(userDetails.getCurrentPeriod())) {
			throw new BusinessException("当前期间" + userDetails.getCurrentPeriod() + "不是该账簿的初始期间，不能修改期初余额");
		}
		this.save(courses);
		// 同时更新 科目余额表的数据
		FiCourseBalance queryExample = new FiCourseBalance();
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setBookId(userDetails.getBookId());
		queryExample.setPeriod(userDetails.getCurrentPeriod());
		List<FiCourseBalance> queryResults = fiCourseBalanceService.selectByExample(queryExample);
		for (FiCourseBalance courseBalance : queryResults) {
			for (FiCourse course : courses) {
				if (course.getCourseNo().equals(courseBalance.getCourseNo())) {
					courseBalance.setStartBalance(courseBalance.getStartBalance() + course.getBalance());
					courseBalance.setEndBalance(courseBalance.getEndBalance() + course.getBalance());
				}
			}
		}
		this.fiCourseBalanceService.updateCourseBalances(queryResults);
	}

	@Override
	public List<FiCourse> selectByKey(String courseNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return fiCourseMapper.selectByKey(courseNo, userDetails.getCompanyId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiCourseMapper;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}
}
