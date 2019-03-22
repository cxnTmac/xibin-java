package com.xibin.wms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmAssembleHeaderMapper;
import com.xibin.wms.pojo.WmAssembleAlloc;
import com.xibin.wms.pojo.WmAssembleHeader;
import com.xibin.wms.query.WmAssembleSDetailQueryItem;
import com.xibin.wms.service.WmAssembleHeaderService;
import com.xibin.wms.service.WmAssembleSDetailService;
import com.xibin.wms.service.WmAssembleUpdateService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmAssembleUpdateServiceImpl implements WmAssembleUpdateService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmAssembleHeaderService wmAssembleHeaderService;
	@Resource
	private WmAssembleSDetailService wmAssembleSDetailService;
	@Resource
	private WmAssembleHeaderMapper wmAssembleHeaderMapper;

	@Override
	public void updateAssembleStatusByOrderNo(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAssembleStatusByAlloc(WmAssembleAlloc alloc) throws BusinessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updataAssembleStatusByAssembleSDetail(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);

		List<WmAssembleHeader> headerQueryResults = wmAssembleHeaderService.selectByKey(orderNo);
		if (headerQueryResults.size() == 0) {
			throw new BusinessException("数据有误，组装单[" + orderNo + "]不存在");
		}
		WmAssembleHeader orderHeander = headerQueryResults.get(0);
		Map sDetailMap = new HashMap<>();
		sDetailMap.put("orderNo", orderNo);
		List<WmAssembleSDetailQueryItem> detailQueryResults = wmAssembleSDetailService
				.getAllAssembleSDetailByOrderNo(sDetailMap);
		if (detailQueryResults.size() == 0) {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]明细不存在");
		}
		int countOfFullPick = 0;
		int countOfPartPick = 0;
		int countOfFullAlloc = 0;
		int countOfPartAlloc = 0;
		for (WmAssembleSDetailQueryItem sDetail : detailQueryResults) {
			if (WmsCodeMaster.ASS_FULL_PICK.getCode().equals(sDetail.getStatus())) {
				countOfFullPick++;
			}
			if (WmsCodeMaster.ASS_PART_PICK.getCode().equals(sDetail.getStatus())) {
				countOfPartPick++;
			}
			if (WmsCodeMaster.ASS_FULL_ALLOC.getCode().equals(sDetail.getStatus())) {
				countOfFullAlloc++;
			}
			if (WmsCodeMaster.ASS_PART_ALLOC.getCode().equals(sDetail.getStatus())) {
				countOfPartAlloc++;
			}
		}
		if (countOfFullPick > 0 || countOfPartPick > 0) {
			if (countOfFullPick == detailQueryResults.size()) {
				orderHeander.setStatus(WmsCodeMaster.ASS_FULL_PICK.getCode());
			} else {
				orderHeander.setStatus(WmsCodeMaster.ASS_PART_PICK.getCode());
			}
		} else if (countOfFullAlloc > 0 || countOfPartAlloc > 0) {
			if (countOfFullAlloc == detailQueryResults.size()) {
				orderHeander.setStatus(WmsCodeMaster.ASS_FULL_ALLOC.getCode());
			} else {
				orderHeander.setStatus(WmsCodeMaster.ASS_PART_ALLOC.getCode());
			}
		} else {
			orderHeander.setStatus(WmsCodeMaster.SO_NEW.getCode());
		}
		DaoUtil.save(orderHeander, wmAssembleHeaderMapper, session);
	}

}
