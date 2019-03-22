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
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.fin.dao.FiBookMapper;
import com.xibin.fin.dao.FiCourseBalanceMapper;
import com.xibin.fin.dao.FiVoucherGLMapper;
import com.xibin.fin.entity.FiCourseBalanceGLQueryItem;
import com.xibin.fin.entity.FiVoucherGLEntity;
import com.xibin.fin.entity.FiVoucherSumByCourseQueryItem;
import com.xibin.fin.pojo.FiBook;
import com.xibin.fin.service.FiVoucherGLService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FiVoucherGLServiceImpl extends BaseManagerImpl implements FiVoucherGLService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiVoucherGLMapper fiVoucherGLMapper;

	@Autowired
	private FiCourseBalanceMapper fiCourseBalanceMapper;
	@Autowired
	private FiBookMapper fiBookMapper;

	@Override
	public List<FiVoucherGLEntity> queryForVoucherGL(Map map) {
		// TODO Auto-generated method stub
		List<FiVoucherGLEntity> queryResult = new ArrayList<FiVoucherGLEntity>();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		FiBook currentBook = fiBookMapper.selectByPrimaryKey(userDetails.getBookId());
		map.put("companyId", userDetails.getCompanyId());
		map.put("bookId", userDetails.getBookId());
		String periodFrom = (String) map.get("periodFrom");
		List<FiCourseBalanceGLQueryItem> courseBalances = fiCourseBalanceMapper.queryCourseBalanceForVoucherGL(map);
		for (FiCourseBalanceGLQueryItem courseBalance : courseBalances) {
			// 如果是该科目的第一个期间
			if (periodFrom.equals(courseBalance.getPeriod())) {
				FiVoucherGLEntity courseStartEntity = new FiVoucherGLEntity();
				courseStartEntity.setBalance(courseBalance.getStartBalance());
				courseStartEntity.setCourseNo(courseBalance.getCourseNo());
				courseStartEntity.setCourseName(courseBalance.getCourseName());
				courseStartEntity.setPeriod(courseBalance.getPeriod());
				courseStartEntity.setToGo(courseBalance.getToGo());
				String startPeriod = currentBook.getBeginYear() + currentBook.getPeriod();
				// 如果是本年起始期间
				if (courseBalance.getPeriod().equals(startPeriod)) {
					courseStartEntity.setSummary("年初余额");
				} else {
					courseStartEntity.setSummary("期初余额");
				}
				queryResult.add(courseStartEntity);
			}
			FiVoucherGLEntity courseSumEntity = new FiVoucherGLEntity();
			courseSumEntity.setBalance(courseBalance.getEndBalance());
			courseSumEntity.setCourseNo(courseBalance.getCourseNo());
			courseSumEntity.setCourseName(courseBalance.getCourseName());
			courseSumEntity.setPeriod(courseBalance.getPeriod());
			courseSumEntity.setToGo(courseBalance.getToGo());
			courseSumEntity.setSumCredit(courseBalance.getSumCredit());
			courseSumEntity.setSumDebit(courseBalance.getSumDebit());
			courseSumEntity.setSummary("本期合计");
			queryResult.add(courseSumEntity);

			FiVoucherGLEntity courseAccumulateEntity = new FiVoucherGLEntity();
			courseAccumulateEntity.setBalance(courseBalance.getEndBalance());
			courseAccumulateEntity.setCourseNo(courseBalance.getCourseNo());
			courseAccumulateEntity.setCourseName(courseBalance.getCourseName());
			courseAccumulateEntity.setPeriod(courseBalance.getPeriod());
			courseAccumulateEntity.setToGo(courseBalance.getToGo());
			courseAccumulateEntity.setSumCredit(courseBalance.getAccumulateCredit());
			courseAccumulateEntity.setSumDebit(courseBalance.getAccumulateDebit());
			courseAccumulateEntity.setSummary("本年累计");
			queryResult.add(courseAccumulateEntity);
		}
		return queryResult;
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiVoucherGLMapper;
	}

	@Override
	public List<FiCourseBalanceGLQueryItem> queryForAccountBalance(Map map) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId());
		map.put("bookId", userDetails.getBookId());
		return fiCourseBalanceMapper.queryCourseBalanceForVoucherGL(map);
	}

	public List<?> queryCredentialSummary(Map map) {
		return null;
	}

	@Override
	public List<FiVoucherSumByCourseQueryItem> queryForVoucherDetailSum(Map map) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId());
		map.put("bookId", userDetails.getBookId());
		return fiVoucherGLMapper.queryForVoucherDetailSum(map);
	}

	@Override
	public Integer queryForVoucherCount(Map map) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		map.put("companyId", userDetails.getCompanyId());
		map.put("bookId", userDetails.getBookId());
		return fiVoucherGLMapper.queryForVoucherCount(map);
	}

}
