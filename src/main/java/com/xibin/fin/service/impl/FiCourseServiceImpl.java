package com.xibin.fin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.ComputeUtil;
import com.xibin.fin.dao.FiCourseBalanceMapper;
import com.xibin.fin.dao.FiCourseMapper;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiCourseBalance;
import com.xibin.fin.service.FiCourseBalanceService;
import com.xibin.fin.service.FiCourseService;
import com.xibin.fin.service.FiTermService;
import com.xibin.fin.service.FiVoucherService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FiCourseServiceImpl extends BaseManagerImpl implements FiCourseService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiCourseMapper fiCourseMapper;
	@Autowired
	private FiCourseBalanceMapper fiCourseBalanceMapper;
	@Autowired
	private FiCourseBalanceService fiCourseBalanceService;
	@Autowired
	private FiVoucherService fiVoucherService;
	@Autowired
	private FiTermService fiTermService;

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
		// 需要修改
		return null;
	}

	@Override
	public Message addChildCourse(FiCourse model, String parentCourseNo) throws BusinessException {
		// TODO Auto-generated method stub
		Message msg = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setBookId(userDetails.getBookId());
		List<FiCourse> parentList = fiCourseMapper.selectByKey(parentCourseNo, model.getCompanyId().toString(),
				model.getBookId().toString());
		if (parentList.size() == 0) {
			throw new BusinessException("父科目编码：[" + model.getCourseNo() + "] 不存在");
		}
		// 父科目 不在是叶子节点
		FiCourse parent = parentList.get(0);
		parent.setIsChild("N");
		this.save(parent);
		// 更新该账簿下的科目余额表，如果是第一个子科目，父科目的数值全部转移到新的子科目上
		Map condition = new HashMap<>();
		condition.put("courseNoStart", parent.getCourseNo());
		condition.put("companyId", userDetails.getCompanyId());
		condition.put("bookId", userDetails.getBookId());
		List<FiCourse> childList = fiCourseMapper.selectAllByCondition(condition);
		int childSize = childList.size();
		if (childSize < 10) {
			model.setCourseNo(parentCourseNo + "0" + childSize);
		} else {
			model.setCourseNo(parentCourseNo + childSize);
		}

		// 更改科目余额表的值
		FiCourseBalance fiCourseBalanceExample = new FiCourseBalance();
		fiCourseBalanceExample.setCompanyId(userDetails.getCompanyId());
		fiCourseBalanceExample.setBookId(userDetails.getBookId());
		fiCourseBalanceExample.setCourseNo(parent.getCourseNo());
		List<FiCourseBalance> courseBalanceList = fiCourseBalanceService.selectByExample(fiCourseBalanceExample);
		List<FiCourseBalance> newCourseBalanceList = new ArrayList<FiCourseBalance>();
		for (FiCourseBalance courseBalance : courseBalanceList) {
			FiCourseBalance newCourseBalance = new FiCourseBalance();
			newCourseBalance.setCourseNo(model.getCourseNo());
			newCourseBalance.setCompanyId(courseBalance.getCompanyId());
			newCourseBalance.setBookId(courseBalance.getBookId());
			newCourseBalance.setPeriod(courseBalance.getPeriod());
			if (childSize == 1) {
				// 父科目的数值转移到子科目
				newCourseBalance.setStartBalance(courseBalance.getStartBalance());
				newCourseBalance.setEndBalance(courseBalance.getEndBalance());
				newCourseBalance.setSumDebit(courseBalance.getSumDebit());
				newCourseBalance.setSumCredit(courseBalance.getSumCredit());
				newCourseBalance.setAccumulateCredit(courseBalance.getAccumulateCredit());
				newCourseBalance.setAccumulateDebit(courseBalance.getAccumulateDebit());
				newCourseBalance.setYearBalance(courseBalance.getYearBalance());

				newCourseBalanceList.add(newCourseBalance);
				// 父科目的余额全部清零
				courseBalance.setStartBalance(0.0);
				courseBalance.setEndBalance(0.0);
				courseBalance.setSumDebit(0.0);
				courseBalance.setSumCredit(0.0);
				courseBalance.setAccumulateCredit(0.0);
				courseBalance.setAccumulateDebit(0.0);
				courseBalance.setYearBalance(0.0);
			} else {
				// 子科目数值均为0
				newCourseBalance.setStartBalance(0.0);
				newCourseBalance.setEndBalance(0.0);
				newCourseBalance.setSumDebit(0.0);
				newCourseBalance.setSumCredit(0.0);
				newCourseBalance.setAccumulateCredit(0.0);
				newCourseBalance.setAccumulateDebit(0.0);
				newCourseBalance.setYearBalance(0.0);

				newCourseBalanceList.add(newCourseBalance);
			}
			DaoUtil.save(newCourseBalanceList, fiCourseBalanceMapper, session);
			DaoUtil.save(courseBalanceList, fiCourseBalanceMapper, session);
		}
		// 新的子科目的层级是父科目+1
		model.setIsParent(parent.getIsParent() + 1);
		model.setIsChild("Y");
		this.saveCourse(model);
		msg.setCode(200);
		msg.setMsg("操作成功");
		return msg;
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
					courseBalance
							.setStartBalance(ComputeUtil.add(courseBalance.getStartBalance(), course.getBalance()));
					courseBalance.setEndBalance(ComputeUtil.add(courseBalance.getEndBalance(), course.getBalance()));
				}
			}
		}
		this.fiCourseBalanceService.updateCourseBalances(queryResults);
	}

	@Override
	public List<FiCourse> selectByKey(String courseNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return fiCourseMapper.selectByKey(courseNo, userDetails.getCompanyId().toString(),
				userDetails.getBookId().toString());
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
