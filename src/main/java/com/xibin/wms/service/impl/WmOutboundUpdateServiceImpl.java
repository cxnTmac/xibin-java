package com.xibin.wms.service.impl;

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
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmOutboundDetailMapper;
import com.xibin.wms.dao.WmOutboundHeaderMapper;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.service.WmOutboundAllocService;
import com.xibin.wms.service.WmOutboundDetailService;
import com.xibin.wms.service.WmOutboundHeaderService;
import com.xibin.wms.service.WmOutboundUpdateService;

@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmOutboundUpdateServiceImpl implements WmOutboundUpdateService {
	@Autowired
	private HttpSession session;
	@Autowired
	private WmOutboundAllocService wmOutboundAllocService;
	@Autowired
	private WmOutboundDetailService wmOutboundDetailService;
	@Autowired
	private WmOutboundHeaderService wmOutboundHeaderService;
	@Autowired
	private WmOutboundHeaderMapper wmOutboundHeaderMapper;
	@Autowired
	private WmOutboundDetailMapper wmOutboundDetailMapper;

	@Override
	public void updateOutboundStatusByOrderNo(String orderNo) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
		allocQueryExample.setOrderNo(orderNo);
		allocQueryExample.setCompanyId(userDetails.getCompanyId());
		allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundAlloc> allocs = wmOutboundAllocService.selectByExample(allocQueryExample);
		Map<String, List<WmOutboundAlloc>> allocMap = new HashMap<String, List<WmOutboundAlloc>>();
		for (WmOutboundAlloc alloc : allocs) {
			if (allocMap.containsKey(alloc.getLineNo())) {
				List<WmOutboundAlloc> allocList = allocMap.get(alloc.getLineNo());
				allocList.add(alloc);
			} else {
				List<WmOutboundAlloc> allocList = new ArrayList<WmOutboundAlloc>();
				allocList.add(alloc);
				allocMap.put(alloc.getLineNo(), allocList);
			}
		}
		for (Map.Entry<String, List<WmOutboundAlloc>> entry : allocMap.entrySet()) {
			String lineNo = entry.getKey();
			WmOutboundDetail detailQueryExample = new WmOutboundDetail();
			detailQueryExample.setOrderNo(orderNo);
			detailQueryExample.setLineNo(lineNo);
			detailQueryExample.setCompanyId(userDetails.getCompanyId());
			detailQueryExample.setWarehouseId(userDetails.getWarehouseId());
			List<WmOutboundDetail> details = wmOutboundDetailService.selectByExample(detailQueryExample);
			if (details.size() > 0) {
				WmOutboundDetail detail = details.get(0);
				double sumOfShip = 0.0;
				double sumOfPick = 0.0;
				double sumOfAlloc = 0.0;
				List<WmOutboundAlloc> detailAllocs = entry.getValue();
				String status = "";
				for (WmOutboundAlloc e : detailAllocs) {
					if (e.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
						sumOfShip += e.getOutboundNum();
					}
					if (e.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())
							|| e.getStatus().equals(WmsCodeMaster.SO_OVER_PICKING.getCode())) {
						sumOfPick += e.getOutboundNum();
					}
					if (e.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())) {
						sumOfAlloc += e.getOutboundNum();
					}
				}
				if (sumOfShip > 0) {
					if (sumOfShip == detail.getOutboundNum()) {
						status = WmsCodeMaster.SO_FULL_SHIPPING.getCode();
					} else {
						status = WmsCodeMaster.SO_PART_SHIPPING.getCode();
					}
				} else if (sumOfPick > 0) {
					if (sumOfPick == detail.getOutboundNum()) {
						status = WmsCodeMaster.SO_FULL_PICKING.getCode();
					} else {
						status = WmsCodeMaster.SO_PART_PICKING.getCode();
					}
				} else {
					if (sumOfAlloc == detail.getOutboundNum()) {
						status = WmsCodeMaster.SO_FULL_ALLOC.getCode();
					} else {
						status = WmsCodeMaster.SO_PART_ALLOC.getCode();
					}
				}
				detail.setStatus(status);
				detail.setOutboundPickNum(sumOfPick);
				detail.setOutboundShipNum(sumOfShip);
				DaoUtil.save(detail, wmOutboundDetailMapper, session);
			} else {
				throw new BusinessException("数据有误，出库单[" + orderNo + "]明细行号[" + lineNo + "]不存在");
			}
		}
		updataOutboundStatusByOutboundDetail(orderNo);
	}

	@Override
	public void updateOutboundStatusByAlloc(WmOutboundAlloc alloc) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundAlloc allocQueryExample = new WmOutboundAlloc();
		allocQueryExample.setOrderNo(alloc.getOrderNo());
		allocQueryExample.setLineNo(alloc.getLineNo());
		allocQueryExample.setCompanyId(userDetails.getCompanyId());
		allocQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundAlloc> allocs = wmOutboundAllocService.selectByExample(allocQueryExample);
		double sumOfShip = 0.0;
		double sumOfPick = 0.0;
		double sumOfAlloc = 0.0;
		for (WmOutboundAlloc e : allocs) {
			if (e.getStatus().equals(WmsCodeMaster.SO_FULL_SHIPPING.getCode())) {
				sumOfShip += e.getOutboundNum();
			}
			if (e.getStatus().equals(WmsCodeMaster.SO_FULL_PICKING.getCode())
					|| e.getStatus().equals(WmsCodeMaster.SO_OVER_PICKING.getCode())) {
				sumOfPick += e.getOutboundNum();
			}
			if (e.getStatus().equals(WmsCodeMaster.SO_FULL_ALLOC.getCode())) {
				sumOfAlloc += e.getOutboundNum();
			}
		}
		WmOutboundDetail detailQueryExample = new WmOutboundDetail();
		detailQueryExample.setOrderNo(alloc.getOrderNo());
		detailQueryExample.setLineNo(alloc.getLineNo());
		detailQueryExample.setCompanyId(userDetails.getCompanyId());
		detailQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundDetail> details = wmOutboundDetailService.selectByExample(detailQueryExample);
		String status = "";
		if (details.size() > 0) {
			WmOutboundDetail detail = details.get(0);
			if (sumOfShip > 0) {
				if (sumOfShip == detail.getOutboundNum()) {
					status = WmsCodeMaster.SO_FULL_SHIPPING.getCode();
				} else {
					status = WmsCodeMaster.SO_PART_SHIPPING.getCode();
				}
			} else if (sumOfPick > 0) {
				if (sumOfPick == detail.getOutboundNum()) {
					status = WmsCodeMaster.SO_FULL_PICKING.getCode();
				} else {
					status = WmsCodeMaster.SO_PART_PICKING.getCode();
				}
			} else {
				if (sumOfAlloc == detail.getOutboundNum()) {
					status = WmsCodeMaster.SO_FULL_ALLOC.getCode();
				} else {
					status = WmsCodeMaster.SO_PART_ALLOC.getCode();
				}
			}
			detail.setStatus(status);
			detail.setOutboundPickNum(sumOfPick);
			detail.setOutboundShipNum(sumOfShip);
			DaoUtil.save(detail, wmOutboundDetailMapper, session);
			updataOutboundStatusByOutboundDetail(detail.getOrderNo());
		} else {
			throw new BusinessException("数据有误，出库单[" + alloc.getOrderNo() + "]明细行号[" + alloc.getLineNo() + "]不存在");
		}
	}

	@Override
	public void updataOutboundStatusByOutboundDetail(String orderNo) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundHeader headerQueryExample = new WmOutboundHeader();
		headerQueryExample.setOrderNo(orderNo);
		headerQueryExample.setCompanyId(userDetails.getCompanyId());
		headerQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundHeader> headerQueryResults = wmOutboundHeaderService.selectByExample(headerQueryExample);
		if (headerQueryResults.size() == 0) {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]不存在");
		}
		WmOutboundHeader orderHeander = headerQueryResults.get(0);
		WmOutboundDetail detailQueryExample = new WmOutboundDetail();
		detailQueryExample.setOrderNo(orderNo);
		detailQueryExample.setCompanyId(userDetails.getCompanyId());
		detailQueryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundDetail> detailQueryResults = wmOutboundDetailService.selectByExample(detailQueryExample);
		if (detailQueryResults.size() == 0) {
			throw new BusinessException("数据有误，出库单[" + orderNo + "]明细不存在");
		}
		int countOfFullShip = 0;
		int countOfPartShip = 0;
		int countOfFullPick = 0;
		int countOfPartPick = 0;
		int countOfFullAlloc = 0;
		int countOfPartAlloc = 0;
		for (WmOutboundDetail detail : detailQueryResults) {
			if (WmsCodeMaster.SO_FULL_SHIPPING.getCode().equals(detail.getStatus())) {
				countOfFullShip++;
			}
			if (WmsCodeMaster.SO_PART_SHIPPING.getCode().equals(detail.getStatus())) {
				countOfPartShip++;
			}
			if (WmsCodeMaster.SO_FULL_PICKING.getCode().equals(detail.getStatus())) {
				countOfFullPick++;
			}
			if (WmsCodeMaster.SO_PART_PICKING.getCode().equals(detail.getStatus())) {
				countOfPartPick++;
			}
			if (WmsCodeMaster.SO_FULL_ALLOC.getCode().equals(detail.getStatus())) {
				countOfFullAlloc++;
			}
			if (WmsCodeMaster.SO_PART_ALLOC.getCode().equals(detail.getStatus())) {
				countOfPartAlloc++;
			}
		}
		if (countOfFullShip > 0 || countOfPartShip > 0) {
			if (countOfFullShip == detailQueryResults.size()) {
				orderHeander.setStatus(WmsCodeMaster.SO_FULL_SHIPPING.getCode());
			} else {
				orderHeander.setStatus(WmsCodeMaster.SO_PART_SHIPPING.getCode());
			}
		} else if (countOfFullPick > 0 || countOfPartPick > 0) {
			if (countOfFullPick == detailQueryResults.size()) {
				orderHeander.setStatus(WmsCodeMaster.SO_FULL_PICKING.getCode());
			} else {
				orderHeander.setStatus(WmsCodeMaster.SO_PART_PICKING.getCode());
			}
		} else if (countOfFullAlloc > 0 || countOfPartAlloc > 0) {
			if (countOfFullAlloc == detailQueryResults.size()) {
				orderHeander.setStatus(WmsCodeMaster.SO_FULL_ALLOC.getCode());
			} else {
				orderHeander.setStatus(WmsCodeMaster.SO_PART_ALLOC.getCode());
			}
		} else {
			orderHeander.setStatus(WmsCodeMaster.SO_NEW.getCode());
		}
		DaoUtil.save(orderHeander, wmOutboundHeaderMapper, session);
	}

}
