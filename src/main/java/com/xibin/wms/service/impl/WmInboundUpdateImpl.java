package com.xibin.wms.service.impl;

import java.util.ArrayList;
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
import com.xibin.core.utils.ComputeUtil;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.service.WmInboundDetailService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmInboundReceiveService;
import com.xibin.wms.service.WmInboundUpdateService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmInboundUpdateImpl implements WmInboundUpdateService {
	@Autowired
	private HttpSession session;
	@Resource
	private WmInboundReceiveService wmInboundReceiveService;
	@Resource
	private WmInboundDetailService wmInboundDetailService;
	@Resource
	private WmInboundDetailMapper wmInboundDetailMapper;
	@Resource
	private WmInboundHeaderService wmInboundHeaderService;

	@Override
	public void updataInboundStatusByInboundReceive(String orderNo) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmInboundRecieve receiveExample = new WmInboundRecieve();
		receiveExample.setOrderNo(orderNo);
		receiveExample.setCompanyId(userDetails.getCompanyId());
		receiveExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInboundRecieve> recieves = wmInboundReceiveService.selectByExample(receiveExample);
		Map<String, List<WmInboundRecieve>> detailMap = new HashMap<String, List<WmInboundRecieve>>();
		for (WmInboundRecieve recieve : recieves) {
			if (!detailMap.containsKey(recieve.getLineNo())) {
				List<WmInboundRecieve> newList = new ArrayList<WmInboundRecieve>();
				newList.add(recieve);
				detailMap.put(recieve.getLineNo(), newList);
			} else {
				List<WmInboundRecieve> list = detailMap.get(recieve.getLineNo());
				list.add(recieve);
			}
		}
		String headerStatus = updateDetailByReceiveMap(detailMap, orderNo);
		updateHeaderStatusByOrderNo(orderNo, headerStatus);
	}

	private String updateDetailByReceiveMap(Map<String, List<WmInboundRecieve>> detailMap, String orderNo)
			throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmInboundDetail detailExample = new WmInboundDetail();
		detailExample.setOrderNo(orderNo);
		detailExample.setCompanyId(userDetails.getCompanyId());
		detailExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmInboundDetail> detailQueryResults = wmInboundDetailService.selectByExample(detailExample);
		// 统计完全收货的明细行
		int countOfFullRecDetail = 0;
		// 统计创建状态的明细行
		int countOfNewDetail = 0;
		for (Map.Entry<String, List<WmInboundRecieve>> entry : detailMap.entrySet()) {
			Double sumOfInboundNum = 0.0;
			List<WmInboundRecieve> recieves = entry.getValue();
			String lineNo = entry.getKey();
			WmInboundDetail detail = getDetailFromListByLineNo(detailQueryResults, lineNo);
			for (WmInboundRecieve recieve : recieves) {
				sumOfInboundNum = ComputeUtil.add(sumOfInboundNum, recieve.getInboundNum());
				// sumOfInboundNum +=recieve.getInboundNum();
			}
			detail.setInboundNum(sumOfInboundNum);
			if (detail.getInboundPreNum().doubleValue() == detail.getInboundNum().doubleValue()) {
				countOfFullRecDetail++;
				detail.setStatus(WmsCodeMaster.INB_FULL_RECEIVING.getCode());
			} else if (detail.getInboundNum().doubleValue() == 0.0) {
				countOfNewDetail++;
				detail.setStatus(WmsCodeMaster.INB_NEW.getCode());
			} else if (detail.getInboundPreNum().doubleValue() > detail.getInboundNum().doubleValue()
					&& detail.getInboundNum().doubleValue() > 0.0) {
				detail.setStatus(WmsCodeMaster.INB_PART_RECEIVING.getCode());
			}
			DaoUtil.save(detail, wmInboundDetailMapper, session);
			// wmInboundDetailService.saveInboundDetail(detail);
		}
		if (countOfFullRecDetail == detailQueryResults.size()) {
			return WmsCodeMaster.INB_FULL_RECEIVING.getCode();
		} else if (countOfNewDetail == detailQueryResults.size()) {
			return WmsCodeMaster.INB_NEW.getCode();
		} else {
			return WmsCodeMaster.INB_PART_RECEIVING.getCode();
		}
	}

	private WmInboundDetail getDetailFromListByLineNo(List<WmInboundDetail> list, String lineNo) {
		for (WmInboundDetail detail : list) {
			if (detail.getLineNo().equals(lineNo)) {
				return detail;
			}
		}
		return null;
	}

	private void updateHeaderStatusByOrderNo(String orderNo, String headerStatus) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmInboundHeader headerExample = new WmInboundHeader();
		headerExample.setOrderNo(orderNo);
		headerExample.setCompanyId(userDetails.getCompanyId());
		headerExample.setWarehouseId(userDetails.getWarehouseId());
		WmInboundHeader header = wmInboundHeaderService.selectByExample(headerExample).get(0);
		header.setStatus(headerStatus);
		wmInboundHeaderService.saveInbound(header);
	}
}
