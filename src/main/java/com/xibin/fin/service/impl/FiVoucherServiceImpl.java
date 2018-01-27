package com.xibin.fin.service.impl;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.fin.dao.FiBookMapper;
import com.xibin.fin.dao.FiCourseBalanceMapper;
import com.xibin.fin.dao.FiVoucherDetailMapper;
import com.xibin.fin.dao.FiVoucherMapper;
import com.xibin.fin.entity.FiVoucherDetailSumQueryItem;
import com.xibin.fin.entity.FiVoucherSumByCourseQueryItem;
import com.xibin.fin.pojo.FiBook;
import com.xibin.fin.pojo.FiCourseBalance;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.service.FiBookService;
import com.xibin.fin.service.FiCourseBalanceService;
import com.xibin.fin.service.FiVoucherService;
import com.xibin.wms.dao.BdAreaMapper;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.service.BdAreaService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class FiVoucherServiceImpl extends BaseManagerImpl implements FiVoucherService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiVoucherMapper fiVoucherMapper;
	@Autowired
	private FiBookService fiBookService;
	@Autowired
	private FiVoucherDetailMapper fiVoucherDetailMapper;
	@Autowired
	private FiCourseBalanceMapper fiCourseBalanceMapper;
	@Autowired
	private FiCourseBalanceService fiCourseBalanceService;
	@Override
	public FiVoucher getVoucherById(int id) {
		// TODO Auto-generated method stub
		return fiVoucherMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<FiVoucher> getAllVoucherByPage(Map map) {
		// TODO Auto-generated method stub
		return fiVoucherMapper.selectAllByPage(map);
	}

	@Override
	public int removeVoucher(int id) throws BusinessException{
		// TODO Auto-generated method stub
		int []ids = {id};
		return this.delete(ids);
	}

	@Override
	public FiVoucher saveVoucher(FiVoucher model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setBookId(userDetails.getBookId());
		List<FiVoucher> list = fiVoucherMapper.selectByKey(model.getVoucherNum().toString(),model.getVoucherWord(),model.getPeriod(),model.getCompanyId().toString(),model.getBookId().toString());
		if(list.size()>0&&model.getId()==0){
			throw new BusinessException("编码：["+model.getVoucherWord()+model.getVoucherNum()+"] 已存在，不能重复！");
		}
		return (FiVoucher) this.save(model);
	}

	@Override
	public List<FiVoucher> selectByKey(String voucherNum,String voucherWord,String period) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return fiVoucherMapper.selectByKey(voucherNum,voucherWord,period,userDetails.getCompanyId().toString(),userDetails.getBookId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiVoucherMapper;
	}

	private boolean deleteBeforeCheck(String areaCode){
		return true;
	}
	@Override
	public Integer getMaxVoucherNum(String voucherWord, String period) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return fiVoucherMapper.getMaxVoucherNum(voucherWord, period, userDetails.getCompanyId().toString(),userDetails.getBookId().toString());
	}
	@Override
	public Message checkVoucher(FiVoucher voucher) throws BusinessException{
		Message message = new Message();
		if(!"00".equals(voucher.getStatus())){
			message.setCode(0);
			message.setMsg("凭证不是创建状态，不能审核！");
			return message;
		}
		updateCourseBalanceForCheckVoucher(voucher);
		voucher.setStatus("10");
		this.save(voucher);
		message.setCode(200);
		message.setMsg("审核成功");
		message.setData(getVoucherById(voucher.getId()));
		return message;
	}
	private Double caculateBalance(String toGo,Double debit,Double credit,String courseNo){
		if(toGo.equals("借")){
			return debit - credit;
		}else if(toGo.equals("贷")){
			return credit-debit;
		}else{
			return debit - credit;
		}
	}
	
	
	
	private void updateCourseBalanceForCheckVoucher(FiVoucher voucher) throws BusinessException{
		String currentPeriod = voucher.getPeriod();
		List<FiVoucherDetailSumQueryItem> sumQuery = fiVoucherDetailMapper.selectByVoucherIdForSum(voucher.getId().toString());
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		
		
		for(FiVoucherDetailSumQueryItem item:sumQuery){
			FiCourseBalance queryExample = new FiCourseBalance();
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCourseNo(item.getCourseNo());
			FiCourseBalance currentCourseBalance = null;
			List<FiCourseBalance> results = fiCourseBalanceMapper.selectByExample(queryExample);
			
			for(FiCourseBalance result:results){
				if(result.getPeriod().equals(currentPeriod)){
					currentCourseBalance = result;
				}
			}
			if(currentCourseBalance!=null){
				currentCourseBalance.setSumCredit(currentCourseBalance.getSumCredit()+item.getSumCredit());
				currentCourseBalance.setSumDebit(currentCourseBalance.getSumDebit()+item.getSumDebit());
					
				currentCourseBalance.setAccumulateCredit(currentCourseBalance.getAccumulateCredit()+item.getSumCredit());
				currentCourseBalance.setAccumulateDebit(currentCourseBalance.getAccumulateDebit()+item.getSumDebit());
					
				currentCourseBalance.setEndBalance(caculateBalance(item.getToGo(),item.getSumDebit(),item.getSumCredit(),item.getCourseNo())+currentCourseBalance.getEndBalance());
				fiCourseBalanceService.saveCourseBalance(currentCourseBalance);
			}else{
				throw new BusinessException("确少本期的科目余额记录，请联系管理员检查数据库是否数据有丢失！");
			}
			
		}
	}
	
	private void updateCourseBalanceForUnCheckVoucher(FiVoucher voucher) throws BusinessException{
		String currentPeriod = voucher.getPeriod();
		List<FiVoucherDetailSumQueryItem> sumQuery = fiVoucherDetailMapper.selectByVoucherIdForSum(voucher.getId().toString());
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		for(FiVoucherDetailSumQueryItem item:sumQuery){
			FiCourseBalance queryExample = new FiCourseBalance();
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCourseNo(item.getCourseNo());
			queryExample.setPeriod(currentPeriod);
			List<FiCourseBalance> results = fiCourseBalanceMapper.selectByExample(queryExample);
			if(results.isEmpty()){
				throw new BusinessException("找不到当前期间的科目余额统计，请联系管理员查看数据库是否数据有误！");
			}else{
				FiCourseBalance currentCourseBalance = results.get(0);
				currentCourseBalance.setSumCredit(currentCourseBalance.getSumCredit()-item.getSumCredit());
				currentCourseBalance.setSumDebit(currentCourseBalance.getSumDebit()-item.getSumDebit());
				
				currentCourseBalance.setAccumulateCredit(currentCourseBalance.getAccumulateCredit()-item.getSumCredit());
				currentCourseBalance.setAccumulateDebit(currentCourseBalance.getAccumulateDebit()-item.getSumDebit());
				
				currentCourseBalance.setEndBalance(currentCourseBalance.getEndBalance()-caculateBalance(item.getType(),item.getSumDebit(),item.getSumCredit(),item.getCourseNo()));
				fiCourseBalanceService.saveCourseBalance(currentCourseBalance);
			}
		}
	}
	
	
	@Override
	public Message cancelCheckVoucher(FiVoucher voucher) throws BusinessException{
		Message message = new Message();
		if(!"10".equals(voucher.getStatus())){
			message.setCode(0);
			message.setMsg("凭证不是审核状态，不能取消审核！");
			return message;
		}
		updateCourseBalanceForUnCheckVoucher(voucher);
		voucher.setStatus("00");
		this.save(voucher);
		message.setCode(200);
		message.setMsg("取消审核成功");
		message.setData(getVoucherById(voucher.getId()));
		return message;
	}
}