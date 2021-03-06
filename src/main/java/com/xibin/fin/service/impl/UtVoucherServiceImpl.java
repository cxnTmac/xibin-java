package com.xibin.fin.service.impl;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.daosupport.DaoUtil;
import com.xibin.core.pojo.Message;
import com.xibin.fin.service.UtVoucherService;
import com.xibin.wms.dao.WmInboundDetailMapper;
import com.xibin.wms.finforwms.facade.UtVoucherFacade;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.service.WmInboundDetailService;
import com.xibin.wms.service.WmOutboundDetailService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class UtVoucherServiceImpl implements UtVoucherService {
	@Autowired
	HttpSession httpSession;
	@Autowired
	private WmInboundDetailService wmInboundDetailService;
	@Autowired
	private WmOutboundDetailService wmOutboundDetailService;
	@Autowired
	private WmInboundDetailMapper wmInboundDetailMapper;
	@Autowired
	private UtVoucherFacade utVoucherFacade;

	@Override
	public Message createVoucher(String[] ids, String period, String type) {
		Message message = new Message();

		List<WmInboundDetail> details = wmInboundDetailService.getInboundDetailByIds(ids);
		Message saveVoucherResult = utVoucherFacade.createVoucher(details, period, "Admin", type);

		if (saveVoucherResult != null && saveVoucherResult.getCode() == 200) {
			String voucherNo = (String) saveVoucherResult.getData();
			for (WmInboundDetail detail : details) {
				detail.setIsCreatedVoucher("Y");
				detail.setVoucherNo(voucherNo);
			}
			DaoUtil.save(details, wmInboundDetailMapper, httpSession);
			message.setCode(200);
			message.setMsg("凭证添加成功，请在财务系统中查看！");
		} else {
			message.setCode(0);
			message.setMsg("凭证添加失败");
		}
		return message;
	}

	@Override
	public Message createVoucherByOutbound(String[] ids, String period, String type) {
		Message message = new Message();

		List<WmOutboundDetail> details = wmOutboundDetailService.getInboundDetailByIds(ids);
		Message saveVoucherResult = utVoucherFacade.createVoucherByOutbound(details, period, "Admin", type);

		if (saveVoucherResult != null && saveVoucherResult.getCode() == 200) {
			String voucherNo = (String) saveVoucherResult.getData();
			for (WmOutboundDetail detail : details) {
				detail.setIsCreateVoucher("Y");
				detail.setVoucherNo(voucherNo);
			}
			DaoUtil.save(details, wmInboundDetailMapper, httpSession);
			message.setCode(200);
			message.setMsg("凭证添加成功，请在财务系统中查看！");
		} else {
			message.setCode(0);
			message.setMsg("凭证添加失败");
		}
		return message;
	}
}
