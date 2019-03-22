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
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.ComputeUtil;
import com.xibin.fin.dao.FiCourseAuxiliaryBalanceMapper;
import com.xibin.fin.dao.FiCourseBalanceMapper;
import com.xibin.fin.dao.FiCourseMapper;
import com.xibin.fin.dao.FiVoucherDetailMapper;
import com.xibin.fin.dao.FiVoucherMapper;
import com.xibin.fin.entity.FiVoucherDetailAuxiliarySumQueryItem;
import com.xibin.fin.entity.FiVoucherDetailQueryItem;
import com.xibin.fin.entity.FiVoucherDetailSumQueryItem;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiCourseAuxiliaryBalance;
import com.xibin.fin.pojo.FiCourseBalance;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.service.FiCourseAuxiliaryBalanceService;
import com.xibin.fin.service.FiCourseBalanceService;
import com.xibin.fin.service.FiVoucherDetailService;
import com.xibin.fin.service.FiVoucherService;
import com.xibin.wms.service.WmToFinService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FiVoucherServiceImpl extends BaseManagerImpl implements FiVoucherService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiVoucherMapper fiVoucherMapper;
	@Autowired
	private FiVoucherDetailMapper fiVoucherDetailMapper;
	@Autowired
	private FiVoucherDetailService fiVoucherDetailService;
	@Autowired
	private FiCourseMapper fiCourseMapper;
	@Autowired
	private FiCourseAuxiliaryBalanceMapper fiCourseAuxiliaryBalanceMapper;
	@Autowired
	private FiCourseAuxiliaryBalanceService fiCourseAuxiliaryBalanceService;
	@Autowired
	private FiCourseBalanceMapper fiCourseBalanceMapper;
	@Autowired
	private FiCourseBalanceService fiCourseBalanceService;
	@Autowired
	private WmToFinService wmToFinService;

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
	public Message removeVoucher(int id) {
		// TODO Auto-generated method stub
		Message message = deleteBeforeCheck(id);
		if (message.getCode() == 0) {
			return message;
		}
		FiVoucher model = (FiVoucher) message.getData();
		List<FiVoucherDetailQueryItem> queryItems = fiVoucherDetailService.selectByVoucherIdForQueryItem(id + "");
		if (queryItems.size() > 0) {
			int[] detailIds = new int[queryItems.size()];
			int i = 0;
			for (FiVoucherDetailQueryItem queryItem : queryItems) {
				detailIds[i] = queryItem.getId();
				i++;
			}
			DaoUtil.delete(detailIds, fiVoucherDetailMapper);
		}
		this.delete(id);
		// 更新来源单据状态
		if ("INB".equals(model.getFromOrderType())) {
			wmToFinService.updateInboundForRemoveVoucher(model.getId());
		}
		if ("INB_COST".equals(model.getFromOrderType())) {
			wmToFinService.updateInboundForRemoveCostVoucher(model.getId());
		}
		if ("OUB_SALE".equals(model.getFromOrderType())) {
			wmToFinService.updateOutboundForRemoveVoucher(model.getId());
		}
		if ("OUB_COST".equals(model.getFromOrderType())) {
			wmToFinService.updateOutboundForRemoveCostVoucher(model.getId());
		}
		message.setCode(200);
		message.setMsg("操作成功");
		return message;
	}

	@Override
	public FiVoucher saveVoucher(FiVoucher model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setBookId(userDetails.getBookId());
		List<FiVoucher> list = fiVoucherMapper.selectByKey(model.getVoucherNum().toString(), model.getVoucherWord(),
				model.getPeriod(), model.getCompanyId().toString(), model.getBookId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getVoucherWord() + model.getVoucherNum() + "] 已存在，不能重复！");
		}
		return (FiVoucher) this.save(model);
	}

	@Override
	public List<FiVoucher> selectByKey(String voucherNum, String voucherWord, String period) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return fiVoucherMapper.selectByKey(voucherNum, voucherWord, period, userDetails.getCompanyId().toString(),
				userDetails.getBookId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiVoucherMapper;
	}

	private Message deleteBeforeCheck(Integer id) {
		Message message = new Message();
		FiVoucher voucher = this.getVoucherById(id);
		if (voucher == null) {
			message.setCode(0);
			message.setMsg("凭证已不存在");
			return message;
		} else if (!"00".equals(voucher.getStatus())) {
			message.setCode(0);
			message.setMsg("凭证不是创建状态，不能删除！");
			return message;
		} else {
			message.setData(voucher);
			message.setCode(200);
			return message;
		}
	}

	@Override
	public Integer getMaxVoucherNum(String voucherWord, String period) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return fiVoucherMapper.getMaxVoucherNum(voucherWord, period, userDetails.getCompanyId().toString(),
				userDetails.getBookId().toString());
	}

	@Override
	public Message checkVoucher(FiVoucher voucher) throws BusinessException {
		Message message = new Message();
		if (!"00".equals(voucher.getStatus())) {
			message.setCode(0);
			message.setMsg("凭证不是创建状态，不能审核！");
			return message;
		}
		// 更新科目余额表
		updateCourseBalanceForCheckVoucher(voucher);
		// 更新科目余额辅助核算表
		updateCourseAuxiliaryBalanceForCheckVoucher(voucher);
		voucher.setStatus("10");
		this.save(voucher);
		// 抛错测试
		// if (true) {
		// throw new BusinessException("确少本期的科目余额记录，请联系管理员检查数据库是否数据有丢失！");
		// }
		message.setCode(200);
		message.setMsg("审核成功");
		message.setData(getVoucherById(voucher.getId()));
		return message;
	}

	private Double caculateBalance(String toGo, Double debit, Double credit, String courseNo) {
		if (toGo.equals("借")) {
			return ComputeUtil.sub(debit, credit);
		} else if (toGo.equals("贷")) {
			return ComputeUtil.sub(credit, debit);
		} else {
			return ComputeUtil.sub(debit, credit);
		}
	}

	private void updateCourseAuxiliaryBalanceForCheckVoucher(FiVoucher voucher) throws BusinessException {
		String currentPeriod = voucher.getPeriod();
		List<FiVoucherDetailAuxiliarySumQueryItem> sumQuery = fiVoucherDetailMapper
				.selectByVoucherIdForAuxiliarySum(voucher.getId().toString());
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		for (FiVoucherDetailAuxiliarySumQueryItem item : sumQuery) {
			FiCourseAuxiliaryBalance queryExample = new FiCourseAuxiliaryBalance();
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setFaCode(item.getCode());
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCourseNo(item.getCourseNo());
			queryExample.setPeriod(currentPeriod);
			List<FiCourseAuxiliaryBalance> results = fiCourseAuxiliaryBalanceMapper.selectByExample(queryExample);
			if (results.size() == 0) {
				FiCourseAuxiliaryBalance target = new FiCourseAuxiliaryBalance();
				target.setAccumulateCredit(item.getSumCredit());
				target.setAccumulateDebit(item.getSumDebit());
				target.setStartBalance(0.0);
				target.setEndBalance(
						caculateBalance(item.getToGo(), item.getSumDebit(), item.getSumCredit(), item.getCourseNo()));
				target.setSumCredit(item.getSumCredit());
				target.setSumDebit(item.getSumDebit());
				target.setYearBalance(0.0);
				target.setBookId(userDetails.getBookId());
				target.setCompanyId(userDetails.getCompanyId());
				target.setFaCode(item.getCode());
				target.setCourseNo(item.getCourseNo());
				target.setPeriod(currentPeriod);
				fiCourseAuxiliaryBalanceService.saveCourseAuxiliaryBalance(target);
			} else {
				FiCourseAuxiliaryBalance target = results.get(0);
				target.setSumCredit(ComputeUtil.add(target.getSumCredit(), item.getSumCredit()));
				target.setSumDebit(ComputeUtil.add(target.getSumDebit(), item.getSumDebit()));

				target.setAccumulateCredit(ComputeUtil.add(target.getAccumulateCredit(), item.getSumCredit()));
				target.setAccumulateDebit(ComputeUtil.add(target.getAccumulateDebit(), item.getSumDebit()));

				target.setEndBalance(ComputeUtil.add(
						caculateBalance(item.getToGo(), item.getSumDebit(), item.getSumCredit(), item.getCourseNo()),
						target.getEndBalance()));
				fiCourseAuxiliaryBalanceService.saveCourseAuxiliaryBalance(target);
			}
		}
	}

	private void updateCourseAuxiliaryBalanceForUnCheckVoucher(FiVoucher voucher) throws BusinessException {
		String currentPeriod = voucher.getPeriod();
		List<FiVoucherDetailAuxiliarySumQueryItem> sumQuery = fiVoucherDetailMapper
				.selectByVoucherIdForAuxiliarySum(voucher.getId().toString());
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		for (FiVoucherDetailAuxiliarySumQueryItem item : sumQuery) {
			FiCourseAuxiliaryBalance queryExample = new FiCourseAuxiliaryBalance();
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setFaCode(item.getCode());
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCourseNo(item.getCourseNo());
			queryExample.setPeriod(currentPeriod);
			List<FiCourseAuxiliaryBalance> results = fiCourseAuxiliaryBalanceMapper.selectByExample(queryExample);
			if (results.isEmpty()) {
				throw new BusinessException("找不到当前期间的科目余额统计，请联系管理员查看数据库是否数据有误！");
			} else {
				FiCourseAuxiliaryBalance currentAuxiliaryCourseBalance = results.get(0);
				currentAuxiliaryCourseBalance.setSumCredit(
						ComputeUtil.sub(currentAuxiliaryCourseBalance.getSumCredit(), item.getSumCredit()));
				currentAuxiliaryCourseBalance
						.setSumDebit(ComputeUtil.sub(currentAuxiliaryCourseBalance.getSumDebit(), item.getSumDebit()));

				currentAuxiliaryCourseBalance.setAccumulateCredit(
						ComputeUtil.sub(currentAuxiliaryCourseBalance.getAccumulateCredit(), item.getSumCredit()));
				currentAuxiliaryCourseBalance.setAccumulateDebit(
						ComputeUtil.sub(currentAuxiliaryCourseBalance.getAccumulateDebit(), item.getSumDebit()));

				currentAuxiliaryCourseBalance.setEndBalance(ComputeUtil.sub(
						currentAuxiliaryCourseBalance.getEndBalance(),
						caculateBalance(item.getToGo(), item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
				fiCourseAuxiliaryBalanceService.saveCourseAuxiliaryBalance(currentAuxiliaryCourseBalance);
			}
		}
	}

	private void updateCourseBalanceForCheckVoucher(FiVoucher voucher) throws BusinessException {
		String currentPeriod = voucher.getPeriod();
		List<FiVoucherDetailSumQueryItem> sumQuery = fiVoucherDetailMapper
				.selectByVoucherIdForSum(voucher.getId().toString());
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);

		for (FiVoucherDetailSumQueryItem item : sumQuery) {
			FiCourseBalance queryExample = new FiCourseBalance();
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCourseNo(item.getCourseNo());
			FiCourseBalance currentCourseBalance = null;
			List<FiCourseBalance> results = fiCourseBalanceMapper.selectByExample(queryExample);

			for (FiCourseBalance result : results) {
				if (result.getPeriod().equals(currentPeriod)) {
					currentCourseBalance = result;
				}
			}
			if (currentCourseBalance != null) {
				currentCourseBalance
						.setSumCredit(ComputeUtil.add(currentCourseBalance.getSumCredit(), item.getSumCredit()));
				currentCourseBalance
						.setSumDebit(ComputeUtil.add(currentCourseBalance.getSumDebit(), item.getSumDebit()));

				currentCourseBalance.setAccumulateCredit(
						ComputeUtil.add(currentCourseBalance.getAccumulateCredit(), item.getSumCredit()));
				currentCourseBalance.setAccumulateDebit(
						ComputeUtil.add(currentCourseBalance.getAccumulateDebit(), item.getSumDebit()));

				currentCourseBalance.setEndBalance(ComputeUtil.add(
						caculateBalance(item.getToGo(), item.getSumDebit(), item.getSumCredit(), item.getCourseNo()),
						currentCourseBalance.getEndBalance()));
				fiCourseBalanceService.saveCourseBalance(currentCourseBalance);

				if (currentCourseBalance.getCourseNo().length() == 8) {

					updateSecondParentCourseBalance(item, currentPeriod, true);

					updateParentCourseBalance(item, currentPeriod, true);
				} else if (currentCourseBalance.getCourseNo().length() == 6) {
					updateParentCourseBalance(item, currentPeriod, true);
				}
			} else {
				throw new BusinessException("缺少本期的科目余额记录，请联系管理员检查数据库是否数据有丢失！");
			}

		}
	}

	private void updateCourseBalanceForUnCheckVoucher(FiVoucher voucher) throws BusinessException {
		String currentPeriod = voucher.getPeriod();
		List<FiVoucherDetailSumQueryItem> sumQuery = fiVoucherDetailMapper
				.selectByVoucherIdForSum(voucher.getId().toString());
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		for (FiVoucherDetailSumQueryItem item : sumQuery) {
			FiCourseBalance queryExample = new FiCourseBalance();
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCourseNo(item.getCourseNo());
			queryExample.setPeriod(currentPeriod);
			List<FiCourseBalance> results = fiCourseBalanceMapper.selectByExample(queryExample);
			if (results.isEmpty()) {
				throw new BusinessException("找不到当前期间的科目余额统计，请联系管理员查看数据库是否数据有误！");
			} else {
				FiCourseBalance currentCourseBalance = results.get(0);
				currentCourseBalance
						.setSumCredit(ComputeUtil.sub(currentCourseBalance.getSumCredit(), item.getSumCredit()));
				currentCourseBalance
						.setSumDebit(ComputeUtil.sub(currentCourseBalance.getSumDebit(), item.getSumDebit()));

				currentCourseBalance.setAccumulateCredit(
						ComputeUtil.sub(currentCourseBalance.getAccumulateCredit(), item.getSumCredit()));
				currentCourseBalance.setAccumulateDebit(
						ComputeUtil.sub(currentCourseBalance.getAccumulateDebit(), item.getSumDebit()));

				currentCourseBalance.setEndBalance(ComputeUtil.sub(currentCourseBalance.getEndBalance(),
						caculateBalance(item.getToGo(), item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
				fiCourseBalanceService.saveCourseBalance(currentCourseBalance);
				if (currentCourseBalance.getCourseNo().length() == 8) {
					updateSecondParentCourseBalance(item, currentPeriod, false);
					updateParentCourseBalance(item, currentPeriod, false);
				} else if (currentCourseBalance.getCourseNo().length() == 6) {
					updateParentCourseBalance(item, currentPeriod, false);
				}
			}
		}
	}

	private void updateSecondParentCourseBalance(FiVoucherDetailSumQueryItem item, String currentPeriod, boolean flag)
			throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		// 三级子科目
		if (item.getCourseNo().length() == 8) {
			// 获取二级父科目
			String secondParentCourseNo = item.getCourseNo().substring(0, 6);
			FiCourseBalance queryExample = new FiCourseBalance();
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCourseNo(secondParentCourseNo);
			queryExample.setPeriod(currentPeriod);
			List<FiCourseBalance> results = fiCourseBalanceMapper.selectByExample(queryExample);
			// 获取二级父科目的科目
			FiCourse courseQueryExample = new FiCourse();
			courseQueryExample.setCompanyId(userDetails.getCompanyId());
			courseQueryExample.setBookId(userDetails.getBookId());
			courseQueryExample.setCourseNo(secondParentCourseNo);
			List<FiCourse> courseResults = fiCourseMapper.selectByExample(courseQueryExample);
			if (results.isEmpty()) {
				throw new BusinessException("找不到当前期间的科目余额统计，请联系管理员查看数据库是否数据有误！");
			} else {
				FiCourseBalance currentCourseBalance = results.get(0);
				FiCourse parentCourse = courseResults.get(0);
				// 审核
				if (flag == true) {
					// 子科目与父科目借贷方向一致
					if (parentCourse.getToGo().equals(item.getToGo())) {
						// 父科目期间贷方累计+=子科目期间贷方累计
						currentCourseBalance.setSumCredit(
								ComputeUtil.add(currentCourseBalance.getSumCredit(), item.getSumCredit()));
						// 父科目期间借方累计+=子科目期间借方累计
						currentCourseBalance
								.setSumDebit(ComputeUtil.add(currentCourseBalance.getSumDebit(), item.getSumDebit()));
						// 父科目本年贷方累计+=子科目本年贷方累计
						currentCourseBalance.setAccumulateCredit(
								ComputeUtil.add(currentCourseBalance.getAccumulateCredit(), item.getSumCredit()));
						// 父科目本年借方累计+=子科目本年借方累计
						currentCourseBalance.setAccumulateDebit(
								ComputeUtil.add(currentCourseBalance.getAccumulateDebit(), item.getSumDebit()));

						currentCourseBalance.setEndBalance(
								ComputeUtil.add(currentCourseBalance.getEndBalance(), caculateBalance(item.getToGo(),
										item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
					} else {
						// 子科目与父科目借贷方向相反

						// 父科目期间贷方累计+=子科目期间借方累计
						currentCourseBalance
								.setSumCredit(ComputeUtil.add(currentCourseBalance.getSumCredit(), item.getSumDebit()));
						// 父科目期间借方累计+=子科目期间贷方累计
						currentCourseBalance
								.setSumDebit(ComputeUtil.add(currentCourseBalance.getSumDebit(), item.getSumCredit()));
						// 父科目本年贷方累计+=子科目本年借方累计
						currentCourseBalance.setAccumulateCredit(
								ComputeUtil.add(currentCourseBalance.getAccumulateCredit(), item.getSumDebit()));
						// 父科目本年借方累计+=子科目本年贷方累计
						currentCourseBalance.setAccumulateDebit(
								ComputeUtil.add(currentCourseBalance.getAccumulateDebit(), item.getSumCredit()));

						currentCourseBalance.setEndBalance(
								ComputeUtil.sub(currentCourseBalance.getEndBalance(), caculateBalance(item.getToGo(),
										item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
					}
				} else {// 取消审核
					// 子科目与父科目借贷方向一致
					if (parentCourse.getToGo().equals(item.getToGo())) {
						// 父科目期间贷方累计-=子科目期间贷方累计
						currentCourseBalance.setSumCredit(
								ComputeUtil.sub(currentCourseBalance.getSumCredit(), item.getSumCredit()));
						// 父科目期间借方累计-=子科目期间借方累计
						currentCourseBalance
								.setSumDebit(ComputeUtil.sub(currentCourseBalance.getSumDebit(), item.getSumDebit()));
						// 父科目本年贷方累计-=子科目本年贷方累计
						currentCourseBalance.setAccumulateCredit(
								ComputeUtil.sub(currentCourseBalance.getAccumulateCredit(), item.getSumCredit()));
						// 父科目本年借方累计-=子科目本年借方累计
						currentCourseBalance.setAccumulateDebit(
								ComputeUtil.sub(currentCourseBalance.getAccumulateDebit(), item.getSumDebit()));

						currentCourseBalance.setEndBalance(
								ComputeUtil.sub(currentCourseBalance.getEndBalance(), caculateBalance(item.getToGo(),
										item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
					} else {
						// 子科目与父科目借贷方向相反
						// 父科目期间贷方累计-=子科目期间借方累计
						currentCourseBalance
								.setSumCredit(ComputeUtil.sub(currentCourseBalance.getSumCredit(), item.getSumDebit()));
						// 父科目期间借方累计-=子科目期间贷方累计
						currentCourseBalance
								.setSumDebit(ComputeUtil.sub(currentCourseBalance.getSumDebit(), item.getSumCredit()));
						// 父科目本年贷方累计-=子科目本年借方累计
						currentCourseBalance.setAccumulateCredit(
								ComputeUtil.sub(currentCourseBalance.getAccumulateCredit(), item.getSumDebit()));
						// 父科目本年借方累计-=子科目本年贷方累计
						currentCourseBalance.setAccumulateDebit(
								ComputeUtil.sub(currentCourseBalance.getAccumulateDebit(), item.getSumCredit()));

						currentCourseBalance.setEndBalance(
								ComputeUtil.add(currentCourseBalance.getEndBalance(), caculateBalance(item.getToGo(),
										item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
					}
				}
				fiCourseBalanceService.saveCourseBalance(currentCourseBalance);
			}
		} else {
			throw new BusinessException("该科目不是三级子科目！");
		}
	}

	public void updateParentCourseBalance(FiVoucherDetailSumQueryItem item, String currentPeriod, boolean flag)
			throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		// 二级子科目或三级子科目
		if (item.getCourseNo().length() == 6 || item.getCourseNo().length() == 8) {
			// 获取一级父科目的科目余额记录
			String secondParentCourseNo = item.getCourseNo().substring(0, 4);
			FiCourseBalance queryExample = new FiCourseBalance();
			queryExample.setCompanyId(userDetails.getCompanyId());
			queryExample.setBookId(userDetails.getBookId());
			queryExample.setCourseNo(secondParentCourseNo);
			queryExample.setPeriod(currentPeriod);
			List<FiCourseBalance> results = fiCourseBalanceMapper.selectByExample(queryExample);
			// 获取二级父科目的科目
			FiCourse courseQueryExample = new FiCourse();
			courseQueryExample.setCompanyId(userDetails.getCompanyId());
			courseQueryExample.setBookId(userDetails.getBookId());
			courseQueryExample.setCourseNo(secondParentCourseNo);
			List<FiCourse> courseResults = fiCourseMapper.selectByExample(courseQueryExample);
			if (results.isEmpty()) {
				throw new BusinessException(
						"找不到科目[" + item.getCourseNo() + "]的父科目[" + secondParentCourseNo + "]，请联系管理员查看数据库是否数据有误！");
			} else if (results.isEmpty()) {
				throw new BusinessException("找不到科目[" + item.getCourseNo() + "]的父科目[" + secondParentCourseNo
						+ "]的科目余额记录，请联系管理员查看数据库是否数据有误！");
			} else {
				FiCourseBalance currentCourseBalance = results.get(0);
				FiCourse parentCourse = courseResults.get(0);
				// 审核
				if (flag == true) {
					// 子科目与父科目借贷方向一致
					if (parentCourse.getToGo().equals(item.getToGo())) {
						// 父科目期间贷方累计+=子科目期间贷方累计
						currentCourseBalance.setSumCredit(
								ComputeUtil.add(currentCourseBalance.getSumCredit(), item.getSumCredit()));
						// 父科目期间借方累计+=子科目期间借方累计
						currentCourseBalance
								.setSumDebit(ComputeUtil.add(currentCourseBalance.getSumDebit(), item.getSumDebit()));
						// 父科目本年贷方累计+=子科目本年贷方累计
						currentCourseBalance.setAccumulateCredit(
								ComputeUtil.add(currentCourseBalance.getAccumulateCredit(), item.getSumCredit()));
						// 父科目本年借方累计+=子科目本年借方累计
						currentCourseBalance.setAccumulateDebit(
								ComputeUtil.add(currentCourseBalance.getAccumulateDebit(), item.getSumDebit()));

						currentCourseBalance.setEndBalance(
								ComputeUtil.add(currentCourseBalance.getEndBalance(), caculateBalance(item.getToGo(),
										item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
					} else {
						// 子科目与父科目借贷方向相反

						// 父科目期间贷方累计+=子科目期间借方累计
						currentCourseBalance
								.setSumCredit(ComputeUtil.add(currentCourseBalance.getSumCredit(), item.getSumDebit()));
						// 父科目期间借方累计+=子科目期间贷方累计
						currentCourseBalance
								.setSumDebit(ComputeUtil.add(currentCourseBalance.getSumDebit(), item.getSumCredit()));
						// 父科目本年贷方累计+=子科目本年借方累计
						currentCourseBalance.setAccumulateCredit(
								ComputeUtil.add(currentCourseBalance.getAccumulateCredit(), item.getSumDebit()));
						// 父科目本年借方累计+=子科目本年贷方累计
						currentCourseBalance.setAccumulateDebit(
								ComputeUtil.add(currentCourseBalance.getAccumulateDebit(), item.getSumCredit()));

						currentCourseBalance.setEndBalance(
								ComputeUtil.sub(currentCourseBalance.getEndBalance(), caculateBalance(item.getToGo(),
										item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
					}

				} else {// 取消审核
					// 子科目与父科目借贷方向一致
					if (parentCourse.getToGo().equals(item.getToGo())) {
						// 父科目期间贷方累计-=子科目期间贷方累计
						currentCourseBalance.setSumCredit(
								ComputeUtil.sub(currentCourseBalance.getSumCredit(), item.getSumCredit()));
						// 父科目期间借方累计-=子科目期间借方累计
						currentCourseBalance
								.setSumDebit(ComputeUtil.sub(currentCourseBalance.getSumDebit(), item.getSumDebit()));
						// 父科目本年贷方累计-=子科目本年贷方累计
						currentCourseBalance.setAccumulateCredit(
								ComputeUtil.sub(currentCourseBalance.getAccumulateCredit(), item.getSumCredit()));
						// 父科目本年借方累计-=子科目本年借方累计
						currentCourseBalance.setAccumulateDebit(
								ComputeUtil.sub(currentCourseBalance.getAccumulateDebit(), item.getSumDebit()));

						currentCourseBalance.setEndBalance(
								ComputeUtil.sub(currentCourseBalance.getEndBalance(), caculateBalance(item.getToGo(),
										item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
					} else {
						// 子科目与父科目借贷方向相反
						// 父科目期间贷方累计-=子科目期间借方累计
						currentCourseBalance
								.setSumCredit(ComputeUtil.sub(currentCourseBalance.getSumCredit(), item.getSumDebit()));
						// 父科目期间借方累计-=子科目期间贷方累计
						currentCourseBalance
								.setSumDebit(ComputeUtil.sub(currentCourseBalance.getSumDebit(), item.getSumCredit()));
						// 父科目本年贷方累计-=子科目本年借方累计
						currentCourseBalance.setAccumulateCredit(
								ComputeUtil.sub(currentCourseBalance.getAccumulateCredit(), item.getSumDebit()));
						// 父科目本年借方累计-=子科目本年贷方累计
						currentCourseBalance.setAccumulateDebit(
								ComputeUtil.sub(currentCourseBalance.getAccumulateDebit(), item.getSumCredit()));

						currentCourseBalance.setEndBalance(
								ComputeUtil.add(currentCourseBalance.getEndBalance(), caculateBalance(item.getToGo(),
										item.getSumDebit(), item.getSumCredit(), item.getCourseNo())));
					}
				}
				fiCourseBalanceService.saveCourseBalance(currentCourseBalance);
			}
		} else {
			throw new BusinessException("该科目不是二级或三级子科目！");
		}
	}

	@Override
	public Message cancelCheckVoucher(FiVoucher voucher) throws BusinessException {
		Message message = new Message();
		if (!"10".equals(voucher.getStatus())) {
			message.setCode(0);
			message.setMsg("凭证不是审核状态，不能取消审核！");
			return message;
		}
		updateCourseBalanceForUnCheckVoucher(voucher);
		updateCourseAuxiliaryBalanceForUnCheckVoucher(voucher);
		voucher.setStatus("00");
		this.save(voucher);
		message.setCode(200);
		message.setMsg("取消审核成功");
		message.setData(getVoucherById(voucher.getId()));
		return message;
	}

	@Override
	public List<FiVoucher> selectByExample(FiVoucher example) {
		// TODO Auto-generated method stub
		return fiVoucherMapper.selectByExample(example);
	}
}
