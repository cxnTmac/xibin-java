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
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.fin.dao.FiCourseAuxiliaryBalanceMapper;
import com.xibin.fin.pojo.FiBook;
import com.xibin.fin.pojo.FiCourseAuxiliaryBalance;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.service.FiBookService;
import com.xibin.fin.service.FiCourseAuxiliaryBalanceService;
import com.xibin.fin.service.FiVoucherService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FiCourseAuxiliaryBalanceServiceImpl extends BaseManagerImpl implements FiCourseAuxiliaryBalanceService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiCourseAuxiliaryBalanceMapper fiCourseAuxiliaryBalanceMapper;
	@Autowired
	private FiBookService fiBookService;
	@Autowired
	private FiVoucherService fiVoucherService;

	@Override
	public FiCourseAuxiliaryBalance getCourseAuxiliaryBalanceById(int id) {
		// TODO Auto-generated method stub
		return fiCourseAuxiliaryBalanceMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<FiCourseAuxiliaryBalance> getAllCourseAuxiliaryBalanceByPage(Map map) {
		// TODO Auto-generated method stub
		return fiCourseAuxiliaryBalanceMapper.selectAllByPage(map);
	}

	@Override
	public int removeCourseAuxiliaryBalance(int id) throws BusinessException {
		// TODO Auto-generated method stub

		int[] ids = { id };
		return this.delete(ids);
	}

	@Override
	public FiCourseAuxiliaryBalance saveCourseAuxiliaryBalance(FiCourseAuxiliaryBalance model) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setBookId(userDetails.getBookId());
		return (FiCourseAuxiliaryBalance) this.save(model);
	}

	@Override
	public List<FiCourseAuxiliaryBalance> selectByKey(String faCode, String period, String courseNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return fiCourseAuxiliaryBalanceMapper.selectByKey(faCode, period, courseNo,
				userDetails.getCompanyId().toString(), userDetails.getBookId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiCourseAuxiliaryBalanceMapper;
	}

	@Override
	public Message addCustomerCourseAuxiliaryBlance(String customerCode, double balance, String courseNo) {
		// TODO Auto-generated method stub
		Message message = new Message();
		// 校验当前期间是否是账簿初始期间
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		FiBook currentBook = fiBookService.getBookById(userDetails.getBookId());
		String startPeriod = currentBook.getBeginYear() + currentBook.getPeriod();
		String currentPeriod = userDetails.getCurrentPeriod();
		if (!currentPeriod.equals(startPeriod)) {
			message.setCode(0);
			message.setMsg("当前期间不是账簿的起始期间，不能设置初始余额！");
			return message;
		}
		// 查找当前期间是否已经有凭证被录入
		FiVoucher example = new FiVoucher();
		example.setPeriod(currentPeriod);
		example.setCompanyId(userDetails.getCompanyId());
		example.setBookId(userDetails.getBookId());
		List<FiVoucher> results = fiVoucherService.selectByExample(example);
		if (results.size() > 0) {
			message.setCode(0);
			message.setMsg("当前期间已经有凭证存在，不能设置初始余额！");
			return message;
		}
		List<FiCourseAuxiliaryBalance> auxiliaryBalances = this.selectByKey("cus" + customerCode, currentPeriod,
				courseNo);
		if (auxiliaryBalances.size() > 0) {
			FiCourseAuxiliaryBalance model = auxiliaryBalances.get(0);
			model.setStartBalance(balance);
			this.saveCourseAuxiliaryBalance(model);
		} else {
			FiCourseAuxiliaryBalance model = new FiCourseAuxiliaryBalance();
			model.setBookId(userDetails.getBookId());
			model.setCompanyId(userDetails.getCompanyId());
			model.setStartBalance(balance);
			model.setEndBalance(balance);
			model.setYearBalance(balance);
			model.setAccumulateCredit(0.0);
			model.setAccumulateDebit(0.0);
			model.setSumCredit(0.0);
			model.setSumDebit(0.0);
			model.setFaCode("cus" + customerCode);
			model.setPeriod(currentPeriod);
			model.setCourseNo(courseNo);
			this.saveCourseAuxiliaryBalance(model);
		}
		message.setCode(200);
		message.setMsg("操作成功");
		return message;
	}

}
