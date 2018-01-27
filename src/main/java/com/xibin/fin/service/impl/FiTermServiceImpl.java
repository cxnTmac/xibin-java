package com.xibin.fin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.xml.internal.ws.wsdl.writer.document.Types;
import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.fin.dao.FiCourseBalanceMapper;
import com.xibin.fin.dao.FiCourseMapper;
import com.xibin.fin.dao.FiTermMapper;
import com.xibin.fin.dao.FiVoucherMapper;
import com.xibin.fin.entity.FiCourseBalanceForwardQueryItem;
import com.xibin.fin.pojo.FiBook;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiCourseBalance;
import com.xibin.fin.pojo.FiTerm;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;
import com.xibin.fin.service.FiBookService;
import com.xibin.fin.service.FiCourseService;
import com.xibin.fin.service.FiTermService;
import com.xibin.fin.service.FiVoucherDetailService;
import com.xibin.fin.service.FiVoucherService;
import com.xibin.wms.dao.BdAreaMapper;
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.SysCompany;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;
import com.xibin.wms.service.BdAreaService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class FiTermServiceImpl extends BaseManagerImpl implements FiTermService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiTermMapper fiTermMapper;
	@Autowired
	private FiVoucherDetailService voucherDetailService;
	@Autowired
	private FiVoucherService fiVoucherService;
	@Autowired
	private FiBookService fiBookService;
	@Autowired
	private FiVoucherMapper fiVoucherMapper;
	@Autowired
	private FiCourseBalanceMapper fiCourseBalanceMapper;

	@Override
	public FiTerm getTermById(int id) {
		// TODO Auto-generated method stub
		return fiTermMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<FiTerm> getAllTermByPage(Map map) {
		// TODO Auto-generated method stub
		return fiTermMapper.selectAllByPage(map);
	}

	@Override
	public int removeTerm(int id, String period) throws BusinessException{
		// TODO Auto-generated method stub
		int []ids = {id};
		return this.delete(ids);
	}

	@Override
	public FiTerm saveTerm(FiTerm model) throws BusinessException{
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setBookId(userDetails.getBookId());
		List<FiTerm> list = fiTermMapper.selectByKey(model.getPeriod(),model.getCompanyId().toString(),model.getBookId().toString());
		if(list.size()>0&&model.getId()==0){
			throw new BusinessException("编码：["+model.getPeriod()+"] 已存在，不能重复！");
		}
		this.save(model);
		return selectByKey(model.getPeriod()).get(0);
	}

	@Override
	public List<FiTerm> selectByKey(String period) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		return fiTermMapper.selectByKey(period,userDetails.getCompanyId().toString(),userDetails.getBookId().toString());
	}
	
	@Override
	public FiTerm getCurrentTerm(Integer companyId){
		FiTerm example = new FiTerm();
		if(companyId == null){
			UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
			example.setCompanyId(userDetails.getCompanyId());
		}else{
			example.setCompanyId(companyId);
		}
		example.setStatus("00");
		List<FiTerm> list = fiTermMapper.selectByExample(example);
		if(list.isEmpty()){
			return null;
		}
		return list.get(0);
		
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiTermMapper;
	}

	private boolean deleteBeforeCheck(String areaCode){
		return true;
	}
	
	private boolean checkBeforeBroughtForwardOrEnd(){
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		List<FiVoucher> vouchers = fiVoucherService.selectByKey(null, null, userDetails.getCurrentPeriod());
		for(FiVoucher voucher:vouchers){
			if(voucher.getStatus().equals("00")){
				return false;
			}
		}
		return true;
		
	}
	@Override
	//结转损益
	public Message lossAndGainBroughtForward(Date date ,String summary,String voucherWord) throws BusinessException{
		if(!checkBeforeBroughtForwardOrEnd()){
			Message message = new Message();
			message.setCode(0);
			message.setMsg("当前期间还有凭证未审核，不能结转损益");
			return message;
		}
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		//查询主营业务收入 和 其他收益 的余额
		Map map = new HashMap<>();
		map.put("companyId", userDetails.getCompanyId());
		map.put("bookId",userDetails.getBookId());
		
		//结转益
		List<Integer> types = new ArrayList<Integer>();
		types.add(14);
		types.add(15);
		map.put("types", types);
		List<FiCourseBalanceForwardQueryItem> sumResults = fiCourseBalanceMapper.queryCourseBalanceForward(map);
		
		
		FiVoucher voucher = new FiVoucher();
		voucher.setVoucherWord(voucherWord);
		voucher.setBillDate(new Date());
		voucher.setPeriod(userDetails.getCurrentPeriod());
		voucher.setStatus("00");


		List<FiVoucherDetail> details = new ArrayList<FiVoucherDetail>();
		int i = 1;
		Double sum = 0.0;
		//设置借方
		for(FiCourseBalanceForwardQueryItem sumResult:sumResults){
			FiVoucherDetail detail = new FiVoucherDetail();
			detail.setDebit(-sumResult.getDebitBalance());
			detail.setCredit(0.0);
			detail.setCourseNo(sumResult.getCourseNo());
			detail.setLineNo(i);
			details.add(detail);
			sum +=sumResult.getDebitBalance();
			i++;
		}
		if(sumResults.size()!= 0){
			FiVoucherDetail detailTotal = new FiVoucherDetail();
			detailTotal.setCourseNo("4103");
			detailTotal.setCredit(-sum);
			detailTotal.setDebit(0.0);
			detailTotal.setLineNo(i);
			detailTotal.setSummary(summary);
			details.add(detailTotal);
			i++;
		}

		//结转损
		types.clear();
		types.add(16);
		types.add(17);
		types.add(18);
		types.add(19);
		map.put("types", types);
		List<FiCourseBalanceForwardQueryItem> lossSumResults = fiCourseBalanceMapper.queryCourseBalanceForward(map);
		Double lossSum = 0.0;
		//设置贷
		for(FiCourseBalanceForwardQueryItem lossSumResult:lossSumResults){
			FiVoucherDetail detail = new FiVoucherDetail();
			detail.setCredit(-lossSumResult.getCreditBalance());
			detail.setCourseNo(lossSumResult.getCourseNo());
			detail.setDebit(0.0);
			detail.setLineNo(i);
			details.add(detail);
			lossSum +=lossSumResult.getCreditBalance();
			i++;
		}
		if(lossSumResults.size()!= 0){
			FiVoucherDetail detailTotal2 = new FiVoucherDetail();
			detailTotal2.setCourseNo("4103");
			detailTotal2.setDebit(-lossSum);
			detailTotal2.setCredit(0.0);
			detailTotal2.setLineNo(i);
			detailTotal2.setSummary(summary);
			details.add(detailTotal2);
		}
		
		if(lossSumResults.size() == 0&&lossSumResults.size() == 0){
			Message message = new Message();
			message.setCode(200);
			message.setMsg("本期没有需要结转损益的科目");
			return message;
		}

		return voucherDetailService.saveVoucherAndDetail(voucher,details,new ArrayList<FiVoucherDetail>());

	}
	
    private Message checkBeforeEndTerm(){
    	Message checkResults = new Message();
    	//校验是否所有凭证都已审核
    	if(!this.checkBeforeBroughtForwardOrEnd()){
    		checkResults.setCode(0);
    		checkResults.setMsg("当前期间还有凭证未审核，不能结转结账");
    		return checkResults;
    	}
    	//校验所有的损益类科目余额是否为0
    	UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		//查询主营业务收入 和 其他收益 的余额
		Map map = new HashMap<>();
		map.put("companyId", userDetails.getCompanyId());
		map.put("bookId",userDetails.getBookId());
		
		//结转益
		List<Integer> types = new ArrayList<Integer>();
		types.add(14);
		types.add(15);
		types.add(16);
		types.add(17);
		types.add(18);
		types.add(19);
		map.put("types", types);
    	List<FiCourseBalanceForwardQueryItem> sumResults = fiCourseBalanceMapper.queryCourseBalanceForward(map);
    	if(sumResults.size()>0){
    		checkResults.setCode(0);
    		checkResults.setMsg("本期损益类科目还有科目未结转，请先结转损益！");
    		return checkResults;
    	}
    	checkResults.setCode(200);;
    	return checkResults;
    }
    
    @Override
	public Message endTerm() throws BusinessException{
		Message checkeResult = checkBeforeEndTerm();
		if(checkeResult.getCode() == 0){
			return checkeResult;
		}
		Message returnMsg = new Message();
		//更新所有凭证的状态，从已审核 更新为  已结账
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		Map map = new HashMap<>();
	    map.put("20", "toStatus");
	    map.put("companyId", userDetails.getCompanyId());
		map.put("bookId",userDetails.getBookId());
		map.put("period", userDetails.getCurrentPeriod());
		fiVoucherMapper.updateStatus(map);
		//更新期间状态为已结账
		FiTerm currentTerm = getCurrentTerm(null);
		if(currentTerm == null){
			throw new BusinessException("没有当前期间的数据，无法结账，请联系管理员！");
		}
		currentTerm.setStatus("10");
		currentTerm.setEndDate(new Date());
		this.save(currentTerm);
		//新增下一个期间以及期间余额表记录
		String currentPeriod = userDetails.getCurrentPeriod();
		Integer currentBookId = userDetails.getBookId();
		String yearStr = currentPeriod.substring(0, 4);
		String monthStr = currentPeriod.substring(4);
		//已到达年底期限
		if(monthStr.equals("02")){
			//更新原来账套的状态
			FiBook currentBook = fiBookService.getBookById(userDetails.getBookId());
			currentBook.setStatus("10");
			currentBook.setEndDate(new Date());
			currentBook.setBookName(currentBook.getBookName()+"-结");
			currentBook.setIsDefault("N");
			fiBookService.saveBook(currentBook);
			
			Integer yeaerInt = Integer.parseInt(yearStr);
			yeaerInt++;
			//创建一个新的账套
			FiBook newBook = new FiBook();
			String companyName = "西濒精工";
			newBook.setBookName(companyName+yeaerInt);
			newBook.setMainCurrency(currentBook.getMainCurrency());
			newBook.setBeginYear(yeaerInt+"");
			newBook.setPeriod("01");
			newBook.setStartDate(new Date());
			newBook.setBaseCourse(currentBook.getBaseCourse());
			newBook.setIsDefault("Y");
			newBook = fiBookService.saveBook(newBook);
			userDetails.setBookId(newBook.getId());
			//创建一个新的期间
			FiTerm newTerm = new FiTerm();
			newTerm.setPeriod(yeaerInt+"01");
			newTerm.setCarryDate(new Date());
			newTerm.setStatus("00");
			newTerm = saveTerm(newTerm);
			userDetails.setCurrentPeriod(newTerm.getPeriod());
			
			//新增下一个期间的期间科目余额表记录
			FiCourseBalance  queryExample = new FiCourseBalance();
			queryExample.setPeriod(currentPeriod);
			queryExample.setBookId(currentBookId);
			queryExample.setCompanyId(userDetails.getCompanyId());
			//查询当前期间的科目余额记录
			List<FiCourseBalance> queryResults = fiCourseBalanceMapper.selectByExample(queryExample);
			for(FiCourseBalance result:queryResults){
				//把ID置空
				result.setId(null);
				//下个月的期初余额为这个月的期末余额
				result.setStartBalance(result.getEndBalance());
				//其他所有统计数据设置为0
				result.setEndBalance(result.getEndBalance());
				result.setSumCredit(0.0);
				result.setSumDebit(0.0);
				result.setAccumulateCredit(0.0);
				result.setAccumulateDebit(0.0);
				//期间设置为新的期间
				result.setPeriod(newTerm.getPeriod());
				//账套ID设置为新的账套ID
				result.setBookId(newBook.getId());
			}
			fiCourseBalanceMapper.insert(queryResults);
			returnMsg.setCode(200);
			returnMsg.setData(userDetails);
			returnMsg.setMsg("年末结账已创建新的账套["+newBook.getBookName()+"],已经新建新的期间["+newTerm.getPeriod()+"]"); 
		}else{
			//新增下一个期间
			Integer monthInt = Integer.parseInt(monthStr);
			monthInt++;
			String newMouthStr = "";
			if(monthInt<10){
				newMouthStr = "0"+monthInt;
			}else{
				newMouthStr = ""+monthInt;
			}
			FiTerm newTerm = new FiTerm();
			newTerm.setPeriod(yearStr+newMouthStr);
			newTerm.setCarryDate(new Date());
			newTerm.setStatus("00");
			newTerm = saveTerm(newTerm);
			userDetails.setCurrentPeriod(newTerm.getPeriod());
			//新增下一个期间的期间科目余额表记录
			FiCourseBalance  queryExample = new FiCourseBalance();
			queryExample.setPeriod(currentPeriod);
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCompanyId(userDetails.getCompanyId());
			//查询当前期间的科目余额记录
			List<FiCourseBalance> queryResults = fiCourseBalanceMapper.selectByExample(queryExample);
			for(FiCourseBalance result:queryResults){
				//把ID置空
				result.setId(null);
				//下个月的期初余额为这个月的期末余额
				result.setStartBalance(result.getEndBalance());
				//设置其他统计数据
				result.setEndBalance(result.getEndBalance());
				result.setSumCredit(0.0);
				result.setSumDebit(0.0);
				result.setAccumulateCredit(result.getAccumulateCredit());
				result.setAccumulateDebit(result.getAccumulateDebit());
				//期间设置为新的期间
				result.setPeriod(newTerm.getPeriod());
			}
			fiCourseBalanceMapper.insert(queryResults);
			returnMsg.setCode(200);
			returnMsg.setData(userDetails);
			returnMsg.setMsg("已经新建新的期间["+newTerm.getPeriod()+"]"); 
		}
		return returnMsg;
		
	}

}
