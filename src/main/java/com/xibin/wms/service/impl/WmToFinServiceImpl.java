package com.xibin.wms.service.impl;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.fabric.xmlrpc.base.Array;
import com.xibin.core.costants.Constants;
import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CodeGenerator;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;
import com.xibin.fin.service.FiVoucherDetailService;
import com.xibin.fin.service.FiVoucherService;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.dao.WmInboundRecieveMapper;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.query.WmInboundDetailQueryItem;
import com.xibin.wms.query.WmInboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmInboundRecieveQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;
import com.xibin.wms.service.WmInboundDetailService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmInboundReceiveService;
import com.xibin.wms.service.WmToFinService;
@Transactional(propagation = Propagation.REQUIRED)
@Service
public class WmToFinServiceImpl   implements WmToFinService {
	@Autowired
	private HttpSession session;
	@Resource
	private FiVoucherService fiVoucherService;
	
	@Resource
	private FiVoucherDetailService voucherDetailService;
	@Override
	public Message accountInboundDetails(List<WmInboundDetailSumPriceQueryItem> sumResults) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		FiVoucher voucher = new FiVoucher();
		voucher.setVoucherWord("记");
		voucher.setBillDate(new Date());
		voucher.setPeriod(userDetails.getCurrentPeriod());
		voucher.setStatus("00");
		List<FiVoucherDetail> details = new ArrayList<FiVoucherDetail>();
		int i = 1;
		Double sum = 0.0;
		for(WmInboundDetailSumPriceQueryItem sumResult:sumResults){
			FiVoucherDetail detail = new FiVoucherDetail();
			detail.setAuxiId(sumResult.getAuxiId());
			detail.setCredit(sumResult.getTotal());
			detail.setDebit(0.0);
			detail.setCourseNo("2202");
			detail.setLineNo(i);
			details.add(detail);
			sum +=sumResult.getTotal();
			i++;
		}
		FiVoucherDetail detailTotal = new FiVoucherDetail();
		detailTotal.setCourseNo("1405");
		detailTotal.setDebit(sum);
		detailTotal.setCredit(0.0);
		detailTotal.setLineNo(i);
		detailTotal.setSummary("采购入库");
		details.add(detailTotal);
		return voucherDetailService.saveVoucherAndDetail(voucher,details,new ArrayList<FiVoucherDetail>());
	}
	
	@Override
	public Message accountOutboundDetails(List<WmOutboundDetailSumPriceQueryItem> sumResults) throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		FiVoucher voucher = new FiVoucher();
		voucher.setVoucherWord("记");
		voucher.setBillDate(new Date());
		voucher.setPeriod(userDetails.getCurrentPeriod());
		voucher.setStatus("00");
		List<FiVoucherDetail> details = new ArrayList<FiVoucherDetail>();
		int i = 1;
		Double sum = 0.0;
		for(WmOutboundDetailSumPriceQueryItem sumResult:sumResults){
			FiVoucherDetail detail = new FiVoucherDetail();
			detail.setAuxiId(sumResult.getAuxiId());
			detail.setDebit(sumResult.getTotal());
			detail.setCredit(0.0);
			detail.setCourseNo("1122");
			detail.setLineNo(i);
			details.add(detail);
			sum +=sumResult.getTotal();
			i++;
		}
		FiVoucherDetail detailTotal = new FiVoucherDetail();
		detailTotal.setCourseNo("6001");
		detailTotal.setCredit(sum);
		detailTotal.setDebit(0.0);
		detailTotal.setLineNo(i);
		detailTotal.setSummary("销售出库");
		details.add(detailTotal);
		return voucherDetailService.saveVoucherAndDetail(voucher,details,new ArrayList<FiVoucherDetail>());
	}
	@Override
	public Message accountOutboundCost(Double sumResults) throws BusinessException {
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		FiVoucher voucher = new FiVoucher();
		voucher.setVoucherWord("记");
		voucher.setBillDate(new Date());
		voucher.setPeriod(userDetails.getCurrentPeriod());
		voucher.setStatus("00");
		List<FiVoucherDetail> details = new ArrayList<FiVoucherDetail>();
		FiVoucherDetail detail = new FiVoucherDetail();
		
		detail.setDebit(sumResults);
		detail.setCredit(0.0);
		detail.setCourseNo("6401");
		detail.setSummary("销售成本");
		detail.setLineNo(1);
		details.add(detail);
		FiVoucherDetail detailTotal = new FiVoucherDetail();
		detailTotal.setCourseNo("1405");
		detailTotal.setCredit(sumResults);
		detailTotal.setDebit(0.0);
		detailTotal.setLineNo(2);
		details.add(detailTotal);
		return voucherDetailService.saveVoucherAndDetail(voucher,details,new ArrayList<FiVoucherDetail>());
		
	}

	
	
	

}
