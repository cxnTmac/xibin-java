package com.xibin.wms.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.ComputeUtil;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;
import com.xibin.fin.service.FiVoucherDetailService;
import com.xibin.fin.service.FiVoucherService;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.dao.WmInboundHeaderMapper;
import com.xibin.wms.dao.WmOutboundHeaderMapper;
import com.xibin.wms.query.WmInboundDetailSumPriceQueryItem;
import com.xibin.wms.query.WmOutboundDetailSumPriceQueryItem;
import com.xibin.wms.service.WmToFinService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class WmToFinServiceImpl implements WmToFinService {
	@Autowired
	private HttpSession session;
	@Resource
	private FiVoucherService fiVoucherService;

	@Resource
	private FiVoucherDetailService voucherDetailService;

	@Resource
	private WmInboundHeaderMapper wmInboundHeaderMapper;

	@Resource
	private WmOutboundHeaderMapper wmOutboundHeaderMapper;

	@Override
	public Message accountInboundDetails(List<WmInboundDetailSumPriceQueryItem> sumResults, String inboundType)
			throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		FiVoucher voucher = new FiVoucher();
		voucher.setVoucherWord("记");
		voucher.setBillDate(new Date());
		voucher.setPeriod(userDetails.getCurrentPeriod());
		voucher.setStatus("00");
		voucher.setFromOrderType("INB");
		List<FiVoucherDetail> details = new ArrayList<FiVoucherDetail>();
		int i = 2;
		Double sum = 0.0;
		// 现购入库
		if (WmsCodeMaster.INB_XG.getCode().equals(inboundType)) {
			// 贷方
			for (WmInboundDetailSumPriceQueryItem sumResult : sumResults) {
				sum += sumResult.getTotal();
			}
			FiVoucherDetail detail = new FiVoucherDetail();
			detail.setCredit(sum);
			detail.setLineNo(2);
			detail.setDebit(0.0);
			detail.setEditable("Y");
			detail.setCourseNo(getInboundCreditCourse(inboundType));
			details.add(detail);
			// detail.setAuxiId(sumResult.getAuxiId());
		} else {
			// 贷方
			for (WmInboundDetailSumPriceQueryItem sumResult : sumResults) {
				FiVoucherDetail detail = new FiVoucherDetail();
				detail.setAuxiId(sumResult.getAuxiId());
				// 退货入库为负数
				if (WmsCodeMaster.INB_RI.getCode().equals(inboundType)) {
					detail.setCredit(-sumResult.getTotal());
				} else {
					detail.setCredit(sumResult.getTotal());
				}
				detail.setDebit(0.0);
				detail.setCourseNo(getInboundCreditCourse(inboundType));
				detail.setLineNo(i);
				detail.setEditable("N");
				details.add(detail);
				sum += sumResult.getTotal();
				i++;
			}
		}

		// 借方
		FiVoucherDetail detailTotal = new FiVoucherDetail();
		detailTotal.setCourseNo(getInboundDebitCourse(inboundType));
		// 退货入库为负数
		if (WmsCodeMaster.INB_RI.getCode().equals(inboundType)) {
			detailTotal.setDebit(-sum);
		} else {
			detailTotal.setDebit(sum);
		}
		detailTotal.setCredit(0.0);
		detailTotal.setLineNo(1);
		detailTotal.setSummary(getInboundSummery(inboundType));
		detailTotal.setEditable("N");
		details.add(detailTotal);
		return voucherDetailService.saveVoucherAndDetail(voucher, details, new ArrayList<FiVoucherDetail>());
	}

	// 入库摘要
	private String getInboundSummery(String inboundType) throws BusinessException {
		if (WmsCodeMaster.INB_CI.getCode().equals(inboundType)) {
			return "赊购入库";
		} else if (WmsCodeMaster.INB_PI.getCode().equals(inboundType)) {
			return "盘盈";
		} else if (WmsCodeMaster.INB_XG.getCode().equals(inboundType)) {
			return "现购入库";
		} else if (WmsCodeMaster.INB_RI.getCode().equals(inboundType)) {
			return "销售退回与少货";
		} else {
			throw new BusinessException("未定义的入库单类型：" + inboundType);
		}
	}

	// 出库销售摘要
	private String getOutboundSaleSummery(String outboundType) throws BusinessException {
		// 赊销出库
		if (WmsCodeMaster.OUB_PO.getCode().equals(outboundType)) {
			return "赊销出库";
		} else if (WmsCodeMaster.OUB_XX.getCode().equals(outboundType)) {
			return "现销出库";
		} else if (WmsCodeMaster.OUB_RO.getCode().equals(outboundType)) {
			return "外购退回与少货";
		} else if (WmsCodeMaster.OUB_CO.getCode().equals(outboundType)) {
			return "盘亏";
		} else {
			throw new BusinessException("未定义的出库单类型：" + outboundType);
		}
	}

	// 出库成本摘要
	private String getOutboundCostSummery(String outboundType) throws BusinessException {
		// 赊销出库
		if (WmsCodeMaster.OUB_PO.getCode().equals(outboundType)) {
			return "赊销成本";
		} else if (WmsCodeMaster.OUB_XX.getCode().equals(outboundType)) {
			return "现销成本";
		} else {
			throw new BusinessException("未定义的出库单类型或不能生成成本凭证的出库类型：" + outboundType);
		}
	}

	// 销售退后入库成本摘要
	private String getInboundCostSummery(String inboundType) throws BusinessException {
		// 赊销出库
		if (WmsCodeMaster.INB_RI.getCode().equals(inboundType)) {
			return "销售退货成本";
		} else {
			throw new BusinessException("未定义的出库单类型或不能生成成本凭证的出库类型：" + inboundType);
		}
	}

	// 出库单贷方科目
	private String getOutboundCreditCourse(String outboundType) throws BusinessException {

		if (WmsCodeMaster.OUB_PO.getCode().equals(outboundType)
				|| WmsCodeMaster.OUB_XX.getCode().equals(outboundType)) {
			// 主营业务收入 赊销售出库/现销出库-贷
			return "5101";
		} else if (WmsCodeMaster.OUB_RO.getCode().equals(outboundType)) {
			// 应付账款 退货出库-贷
			return "2121";
		} else if (WmsCodeMaster.OUB_CO.getCode().equals(outboundType)) {
			// 库存商品 盘亏出库-贷
			return "1243";
		} else {
			throw new BusinessException("未定义的出库单类型：" + outboundType);
		}
	}

	// 出库单借方科目
	private String getOutboundDebitCourse(String outboundType) throws BusinessException {
		if (WmsCodeMaster.OUB_PO.getCode().equals(outboundType)) {
			// 应收账款 赊销出库-借
			return "1131";
		} else if (WmsCodeMaster.OUB_XX.getCode().equals(outboundType)) {
			// 库存现金 现销出库-借
			return "1001";
		} else if (WmsCodeMaster.OUB_RO.getCode().equals(outboundType)) {
			// 库存商品 赊销出库-借
			return "1243";
		} else if (WmsCodeMaster.OUB_CO.getCode().equals(outboundType)) {
			// 待处理流动资产损益 盘亏出库-借
			return "191101";
		} else {
			throw new BusinessException("未定义的出库单类型：" + outboundType);
		}
	}

	// 入库单贷方科目
	private String getInboundCreditCourse(String inboundType) throws BusinessException {
		if (WmsCodeMaster.INB_CI.getCode().equals(inboundType)) {
			// 应付账款 赊购入库-贷
			return "2121";
		} else if (WmsCodeMaster.INB_PI.getCode().equals(inboundType)) {
			// 待处理流动资产损益 盘盈入库-贷
			return "191101";
		} else if (WmsCodeMaster.INB_XG.getCode().equals(inboundType)) {
			// 库存现金 现场购入库-贷
			return "1001";
		} else if (WmsCodeMaster.INB_RI.getCode().equals(inboundType)) {
			// 主营业务收入 退货入库-贷
			return "5101";
		} else {
			throw new BusinessException("未定义的入库单类型：" + inboundType);
		}
	}

	// 入库单借方科目
	private String getInboundDebitCourse(String inboundType) throws BusinessException {
		if (WmsCodeMaster.INB_RI.getCode().equals(inboundType)) {
			// 应收账款 退货入库-借
			return "1131";
		} else if (WmsCodeMaster.INB_PI.getCode().equals(inboundType)
				|| WmsCodeMaster.INB_XG.getCode().equals(inboundType)
				|| WmsCodeMaster.INB_CI.getCode().equals(inboundType)) {
			// 库存商品 现购入库 赊购入库 盘盈入库 - 借
			return "1243";
		} else {
			throw new BusinessException("未定义的入库单类型：" + inboundType);
		}
	}

	@Override
	public void updateInboundForRemoveVoucher(Integer voucherId) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map updateMap = new HashMap<>();
		updateMap.put("companyId", userDetails.getCompanyId());
		updateMap.put("warehouseId", userDetails.getWarehouseId());
		updateMap.put("isCalculated", "N");
		updateMap.put("toVoucherId", "0");
		updateMap.put("voucherId", voucherId);
		wmInboundHeaderMapper.updateStatusByOrderNos(updateMap);
	}

	public void updateInboundForRemoveCostVoucher(Integer costVoucherId) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map updateMap = new HashMap<>();
		updateMap.put("companyId", userDetails.getCompanyId());
		updateMap.put("warehouseId", userDetails.getWarehouseId());
		updateMap.put("isCostCalculated", "N");
		updateMap.put("toCostVoucherId", "0");
		updateMap.put("costVoucherId", costVoucherId);
		wmInboundHeaderMapper.updateCostCalculateByOrderNos(updateMap);
	}

	@Override
	public void updateOutboundForRemoveVoucher(Integer voucherId) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map updateMap = new HashMap<>();
		updateMap.put("companyId", userDetails.getCompanyId());
		updateMap.put("warehouseId", userDetails.getWarehouseId());
		updateMap.put("isCalculated", "N");
		updateMap.put("toVoucherId", "0");
		updateMap.put("voucherId", voucherId);
		wmOutboundHeaderMapper.updateCalculateByOrderNos(updateMap);
	}

	@Override
	public void updateOutboundForRemoveCostVoucher(Integer costVoucherId) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map updateMap = new HashMap<>();
		updateMap.put("companyId", userDetails.getCompanyId());
		updateMap.put("warehouseId", userDetails.getWarehouseId());
		updateMap.put("isCostCalculated", "N");
		updateMap.put("toCostVoucherId", "0");
		updateMap.put("costVoucherId", costVoucherId);
		wmOutboundHeaderMapper.updateCostCalculateByOrderNos(updateMap);
	}

	@Override
	public Message accountOutboundDetails(List<WmOutboundDetailSumPriceQueryItem> sumResults, String outboundType)
			throws BusinessException {
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		FiVoucher voucher = new FiVoucher();
		voucher.setVoucherWord("记");
		voucher.setBillDate(new Date());
		voucher.setPeriod(userDetails.getCurrentPeriod());
		voucher.setStatus("00");
		voucher.setFromOrderType("OUB_SALE");
		List<FiVoucherDetail> details = new ArrayList<FiVoucherDetail>();
		int i = 1;
		Double sum = 0.0;
		// 借方
		if (WmsCodeMaster.OUB_XX.getCode().equals(outboundType)) {
			// 现销出库
			for (WmOutboundDetailSumPriceQueryItem sumResult : sumResults) {
				sum = ComputeUtil.add(sum, sumResult.getTotal());
				// sum += sumResult.getTotal();
			}
			FiVoucherDetail detail = new FiVoucherDetail();
			detail.setDebit(sum);
			detail.setCredit(0.0);
			detail.setCourseNo(getOutboundDebitCourse(outboundType));
			detail.setLineNo(i);
			i++;
			// 现销出库的借方 -- 库存现金允许编辑
			detail.setEditable("Y");
			detail.setSummary(getOutboundSaleSummery(outboundType));
			details.add(detail);
		} else {
			for (WmOutboundDetailSumPriceQueryItem sumResult : sumResults) {
				FiVoucherDetail detail = new FiVoucherDetail();
				detail.setAuxiId(sumResult.getAuxiId());
				// 退货出库为负数
				if (WmsCodeMaster.OUB_RO.getCode().equals(outboundType)) {
					detail.setDebit(-sumResult.getTotal());
				} else {
					detail.setDebit(sumResult.getTotal());
				}
				detail.setCredit(0.0);
				detail.setCourseNo(getOutboundDebitCourse(outboundType));
				detail.setLineNo(i);
				detail.setEditable("N");
				if (i == 1) {
					detail.setSummary(getOutboundSaleSummery(outboundType));
				}
				details.add(detail);
				sum = ComputeUtil.add(sum, sumResult.getTotal());
				// sum += sumResult.getTotal();
				i++;
			}
		}

		// 贷方
		FiVoucherDetail detailTotal = new FiVoucherDetail();
		detailTotal.setCourseNo(getOutboundCreditCourse(outboundType));
		// 退货出库为负数
		if (WmsCodeMaster.OUB_RO.getCode().equals(outboundType)) {
			detailTotal.setCredit(-sum);
		} else {
			detailTotal.setCredit(sum);
		}
		detailTotal.setDebit(0.0);
		detailTotal.setLineNo(i);
		detailTotal.setEditable("N");
		details.add(detailTotal);
		return voucherDetailService.saveVoucherAndDetail(voucher, details, new ArrayList<FiVoucherDetail>());
	}

	@Override
	public Message accountOutboundCost(Double sumResults, String outboundType) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		FiVoucher voucher = new FiVoucher();
		voucher.setVoucherWord("记");
		voucher.setBillDate(new Date());
		voucher.setPeriod(userDetails.getCurrentPeriod());
		voucher.setStatus("00");
		voucher.setFromOrderType("OUB_COST");
		List<FiVoucherDetail> details = new ArrayList<FiVoucherDetail>();
		FiVoucherDetail detail = new FiVoucherDetail();
		// 借方
		detail.setDebit(sumResults);
		detail.setCredit(0.0);
		detail.setCourseNo("5401");// 主营业务成本
		detail.setSummary(getOutboundCostSummery(outboundType));
		detail.setLineNo(1);
		detail.setEditable("N");
		details.add(detail);
		FiVoucherDetail detailTotal = new FiVoucherDetail();
		// 贷方
		detailTotal.setCourseNo("1243");// 库存商品
		detailTotal.setCredit(sumResults);
		detailTotal.setDebit(0.0);
		detailTotal.setLineNo(2);
		detailTotal.setEditable("N");
		details.add(detailTotal);
		return voucherDetailService.saveVoucherAndDetail(voucher, details, new ArrayList<FiVoucherDetail>());

	}

	// 外购退货入库单需要核算成本
	@Override
	public Message accountInboundCost(Double sumResults, String inboundType) throws BusinessException {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		FiVoucher voucher = new FiVoucher();
		voucher.setVoucherWord("记");
		voucher.setBillDate(new Date());
		voucher.setPeriod(userDetails.getCurrentPeriod());
		voucher.setStatus("00");
		voucher.setFromOrderType("INB_COST");
		List<FiVoucherDetail> details = new ArrayList<FiVoucherDetail>();
		FiVoucherDetail detail = new FiVoucherDetail();
		// 借方
		detail.setDebit(-sumResults);
		detail.setCredit(0.0);
		detail.setCourseNo("5401");// 主营业务成本
		detail.setSummary(getInboundCostSummery(inboundType));
		detail.setLineNo(1);
		detail.setEditable("N");
		details.add(detail);
		FiVoucherDetail detailTotal = new FiVoucherDetail();
		// 贷方
		detailTotal.setCourseNo("1243");// 库存商品
		detailTotal.setCredit(-sumResults);
		detailTotal.setDebit(0.0);
		detailTotal.setLineNo(2);
		detailTotal.setEditable("N");
		details.add(detailTotal);
		return voucherDetailService.saveVoucherAndDetail(voucher, details, new ArrayList<FiVoucherDetail>());

	}
}
