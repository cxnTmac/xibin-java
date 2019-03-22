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
import com.xibin.fin.dao.FiVoucherDetailMapper;
import com.xibin.fin.entity.FiVoucherDetailQueryItem;
import com.xibin.fin.entity.FiVoucherEntity;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;
import com.xibin.fin.service.FiVoucherDetailService;
import com.xibin.fin.service.FiVoucherService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FiVoucherDetailServiceImpl extends BaseManagerImpl implements FiVoucherDetailService {
	@Autowired
	private HttpSession session;
	@Autowired
	private FiVoucherDetailMapper fiVoucherDetailMapper;
	@Autowired
	private FiVoucherService fiVoucherService;

	@Override
	public FiVoucherDetail getVoucherDetailById(int id) {
		// TODO Auto-generated method stub
		return fiVoucherDetailMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<FiVoucherDetail> getAllVoucherDetailByPage(Map map) {
		// TODO Auto-generated method stub
		return fiVoucherDetailMapper.selectAllByPage(map);
	}

	@Override
	public int removeVoucherDetail(int id) throws BusinessException {
		// TODO Auto-generated method stub
		int[] ids = { id };
		return this.delete(ids);
	}

	@Override
	public int removeVoucherDetail(int[] ids) throws BusinessException {
		// TODO Auto-generated method stub
		return this.delete(ids);
	}

	@Override
	public FiVoucherDetail saveVoucherDetail(FiVoucherDetail model) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		model.setCompanyId(userDetails.getCompanyId());
		model.setBookId(userDetails.getBookId());
		List<FiVoucherDetail> list = fiVoucherDetailMapper.selectByKey(model.getVoucherId().toString(),
				model.getLineNo().toString(), model.getCompanyId().toString(), model.getBookId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("行号：[" + model.getLineNo() + "] 已存在，不能重复！");
		}
		return (FiVoucherDetail) this.save(model);
	}

	@Override
	public List<FiVoucherDetail> selectByKey(String voucherId, String lineNo) {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		return fiVoucherDetailMapper.selectByKey(voucherId, lineNo, userDetails.getCompanyId().toString(),
				userDetails.getBookId().toString());
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return fiVoucherDetailMapper;
	}

	private boolean deleteBeforeCheck(String areaCode) {
		return true;
	}

	@Override
	public List<FiVoucherEntity> getAllVoucherEntityByPage(Map map) {
		// TODO Auto-generated method stub
		return fiVoucherDetailMapper.selectAllEntityByPage(map);
	}

	@Override
	public List<FiVoucherDetailQueryItem> selectByVoucherIdForQueryItem(String voucherId) {
		// TODO Auto-generated method stub
		return fiVoucherDetailMapper.selectByVoucherIdForQueryItem(voucherId);
	}

	@Override
	public Message saveVoucherAndDetail(FiVoucher voucher, List<FiVoucherDetail> details,
			List<FiVoucherDetail> removeDetails) throws BusinessException {
		Message msg = new Message();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		double sumDebit = 0.0;
		double sumCredit = 0.0;
		if (null != voucher.getFromOrderType() && !"".equals(voucher.getFromOrderType()) && voucher.getId() != null) {
			// throw new BusinessException("由业务单据生成的凭证不能进行编辑更新");
			for (int i = 0; i < details.size(); i++) {

				for (int j = 0; j < details.size(); j++) {
					if (i == j) {
						continue;
					} else if (details.get(i).getCourseNo().equals(details.get(j).getCourseNo())) {
						// 科目一样的情况
						// i是新增的，j不是新增且不可编辑
						if (details.get(i).getId() == null && "N".equals(details.get(j).getEditable())) {
							throw new BusinessException("科目[" + details.get(j).getCourseNo() + "]在此凭证不可编辑，不能新增该科目的分录");
						}
						if (details.get(j).getId() == null && "N".equals(details.get(i).getEditable())) {
							throw new BusinessException("科目[" + details.get(j).getCourseNo() + "]在此凭证不可编辑，不能新增该科目的分录");
						}
						// i不是新增的且不可编辑，j是新增的
					}
				}
			}
		}
		for (FiVoucherDetail detail : removeDetails) {
			if ("N".equals(detail.getEditable())) {
				throw new BusinessException("不可编辑的分录不能删除！");
			}
		}
		for (FiVoucherDetail detail : details) {
			sumDebit += detail.getDebit();
			sumCredit += detail.getCredit();
		}
		if (sumDebit != sumCredit) {
			throw new BusinessException("借贷不平衡，不能保存");
		}
		FiVoucher savedVoucher = new FiVoucher();
		Integer max = fiVoucherService.getMaxVoucherNum(voucher.getVoucherWord(), userDetails.getCurrentPeriod());
		if (max == null) {
			max = 0;
		}
		if (voucher.getId() == null) {
			voucher.setVoucherNum(max + 1);
			voucher.setCompanyId(userDetails.getCompanyId());
			voucher.setBookId(userDetails.getBookId());
		}
		try {
			savedVoucher = fiVoucherService.saveVoucher(voucher);

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.setCode(0);
			msg.setMsg(e.getMessage());
			return msg;
		}
		// ID不为空说明是更新而不是新增
		if (voucher.getId() != null) {
			// 判断原来是否有分录
			if (removeDetails.size() > 0) {
				int[] ids = new int[removeDetails.size()];
				for (int i = 0; i < ids.length; i++) {
					ids[i] = removeDetails.get(i).getId();
				}
				// 把原来的分录全部删除
				this.removeVoucherDetail(ids);
			}
		}
		List<FiVoucher> saveResults = fiVoucherService.selectByKey((max + 1) + "", voucher.getVoucherWord(),
				voucher.getPeriod());
		if (saveResults.size() > 0) {
			savedVoucher = saveResults.get(0);
		}
		msg.setData(savedVoucher);
		for (FiVoucherDetail detail : details) {
			detail.setVoucherId(savedVoucher.getId());
			detail.setCompanyId(userDetails.getCompanyId());
			detail.setBookId(userDetails.getBookId());
		}
		// 一次性多条保存
		this.save(details);
		msg.setMsg("凭证：" + savedVoucher.getVoucherWord() + savedVoucher.getVoucherNum() + "保存成功！");
		msg.setCode(200);
		return msg;
	}
}
