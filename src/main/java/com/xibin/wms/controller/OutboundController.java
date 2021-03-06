package com.xibin.wms.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.ocr.AipOcr;
import com.xibin.core.costants.Constants;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.CustomizedPropertyConfigurer;
import com.xibin.fin.service.UtVoucherService;
import com.xibin.wms.constants.WmsCodeMaster;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmOutboundAllocQueryItem;
import com.xibin.wms.query.WmOutboundDetailPriceQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundDetailSaleHistoryQueryItem;
import com.xibin.wms.query.WmOutboundDetailSkuQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.WmOutboundAllocService;
import com.xibin.wms.service.WmOutboundDetailService;
import com.xibin.wms.service.WmOutboundHeaderService;

@Controller
@RequestMapping(value = "/outbound", produces = { "application/json;charset=UTF-8" })
public class OutboundController {
	@Autowired
	private HttpSession session;
	@Resource
	private WmOutboundHeaderService outboundHeaderService;
	@Resource
	private WmOutboundDetailService outboundDetailService;
	@Resource
	private WmOutboundAllocService outboundAllocService;
	@Autowired
	private UtVoucherService utVoucherService;

	@RequestMapping("/showAllOutboundOrder")
	@ResponseBody
	public PageEntity<WmOutboundHeaderQueryItem> showAllOutboundOrder(HttpServletRequest request, Model model) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		// 开始分页
		PageEntity<WmOutboundHeaderQueryItem> pageEntity = new PageEntity<WmOutboundHeaderQueryItem>();
		Page<?> page = new Page();
		Map map = new HashMap<>();
		if (request.getParameter("page") != null && request.getParameter("size") != null) {
			// 配置分页参数
			page.setPageNo(Integer.parseInt(request.getParameter("page")));
			page.setPageSize(Integer.parseInt(request.getParameter("size")));
			map = JSONObject.parseObject(request.getParameter("conditions"));
			map.put("page", page);
		} else {
			map = JSONObject.parseObject(request.getParameter("conditions"));
		}
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmOutboundHeaderQueryItem> list = outboundHeaderService.getAllOutboundOrderByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllOutboundDetail")
	@ResponseBody
	public PageEntity<WmOutboundDetailQueryItem> showAllOutboundDetail(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmOutboundDetailQueryItem> pageEntity = new PageEntity<WmOutboundDetailQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmOutboundDetailQueryItem> list = outboundDetailService.getAllOutboundDetailByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllClosedOrderOutboundDetail")
	@ResponseBody
	public List<WmOutboundDetailQueryItem> showAllClosedOrderOutboundDetail(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		return outboundDetailService.selectClosedOrderDetail(map);

	}

	@RequestMapping("/showAllOutboundAlloc")
	@ResponseBody
	public PageEntity<WmOutboundAllocQueryItem> showAllOutboundAlloc(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmOutboundAllocQueryItem> pageEntity = new PageEntity<WmOutboundAllocQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmOutboundAllocQueryItem> list = outboundAllocService.getAllOutboundAllocByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/saveOutboundOrder")
	@ResponseBody
	public Message saveOutboundOrder(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("order");
		WmOutboundHeader bean = JSON.parseObject(str, WmOutboundHeader.class);
		try {
			WmOutboundHeaderQueryItem queryItem = this.outboundHeaderService.saveOutbound(bean);
			message.setCode(200);
			message.setData(queryItem);
			message.setMsg("操作成功！");

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public Message remove(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		try {
			for (String orderNo : orderNos) {
				outboundHeaderService.remove(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	@RequestMapping("/removeOutboundDetail")
	@ResponseBody
	public Message removeOutboundDetail(@RequestParam("ids") String idsStr, @RequestParam("orderNo") String orderNo) {
		Message message = new Message();
		String[] idsStrs = idsStr.split(",");
		if (idsStrs.length == 0) {
			message.setCode(0);
			message.setMsg("参数有误，请联系管理员");
			return message;
		}
		int[] ids = new int[idsStrs.length];
		for (int i = 0; i < idsStrs.length; i++) {
			ids[i] = Integer.parseInt(idsStrs[i]);
		}
		try {
			return this.outboundDetailService.removeOutboundDetail(ids, orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/removeOutboundDetailAndCreateNewOrder")
	@ResponseBody
	public Message removeOutboundDetailAndCreateNewOrder(@RequestParam("ids") String idsStr,
			@RequestParam("orderNo") String orderNo) {
		Message message = new Message();
		String[] idsStrs = idsStr.split(",");
		if (idsStrs.length == 0) {
			message.setCode(0);
			message.setMsg("参数有误，请联系管理员");
			return message;
		}
		int[] ids = new int[idsStrs.length];
		for (int i = 0; i < idsStrs.length; i++) {
			ids[i] = Integer.parseInt(idsStrs[i]);
		}
		try {
			return this.outboundDetailService.removeOutboundDetailAndCreateNewOrder(ids, orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/saveOutboundDetail")
	@ResponseBody
	public Message saveOutboundDetail(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("detail");

		WmOutboundDetail bean = JSON.parseObject(str, WmOutboundDetail.class);
		try {
			WmOutboundDetailQueryItem item = this.outboundDetailService.saveOutboundDetail(bean);
			message.setData(item);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	// 只用于更新分配明细的成本
	@RequestMapping("/saveOutboundDetailAlloc")
	@ResponseBody
	public Message saveOutboundDetailAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("detailAlloc");

		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			WmOutboundAlloc item = this.outboundAllocService.saveOutboundAllocForEditCost(bean);
			message.setData(item);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/close")
	@ResponseBody
	public Message audit(@RequestParam("orderNos") String orderNos) {
		Message message = new Message();
		String[] orderArray = orderNos.split(",");
		List<String> errors = new ArrayList<String>();
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			for (String orderNo : orderArray) {
				queryItem = outboundHeaderService.close(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderArray.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	private boolean hasSkuCodeInDetails(List<WmOutboundDetail> details, String skuCode) {
		for (WmOutboundDetail detail : details) {
			if (detail.getSkuCode().equals(skuCode)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping("/batchSaveOutboundDetail")
	@ResponseBody
	public Message batchSaveOutboundDetail(@RequestParam("skuCodes") String skuCodes,
			@RequestParam("prices") String prices, @RequestParam("orderNo") String orderNo,
			@RequestParam("buyerCode") String buyerCode) {
		Message message = new Message();
		String[] skuArray = skuCodes.split(",");
		String[] priceArray = prices.split(",");
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		WmOutboundDetail queryExample = new WmOutboundDetail();
		queryExample.setOrderNo(orderNo);
		queryExample.setCompanyId(userDetails.getCompanyId());
		queryExample.setWarehouseId(userDetails.getWarehouseId());
		List<WmOutboundDetail> details = this.outboundDetailService.selectByExample(queryExample);
		List<String> errors = new ArrayList<String>();
		try {
			for (int i = 0; i < skuArray.length; i++) {
				if (!hasSkuCodeInDetails(details, skuArray[i])) {
					WmOutboundDetail bean = new WmOutboundDetail();
					bean.setOrderNo(orderNo);
					bean.setBuyerCode(buyerCode);
					bean.setOutboundAllocNum(0.0);
					bean.setOutboundNum(0.0);
					bean.setOutboundPickNum(0.0);
					bean.setOutboundShipNum(0.0);
					bean.setOutboundPrice(Double.parseDouble(priceArray[i]));
					bean.setPlanShipLoc("SORTATION");
					bean.setStatus(WmsCodeMaster.SO_NEW.getCode());
					bean.setSkuCode(skuArray[i]);
					this.outboundDetailService.saveOutboundDetail(bean);
				} else {
					errors.add("已存在产品编码[" + skuArray[i] + "]的出库明细");
				}
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() > 0) {
			message.setMsgs(errors);
			message.setCode(100);
			message.converMsgsToMsg("");
		} else {
			message.setCode(200);
			message.setMsg("操作成功！");
		}
		return message;
	}

	@RequestMapping("/getOutboundHeaderByOderNo")
	@ResponseBody
	public WmOutboundHeaderQueryItem getOutboundHeaderByOderNo(HttpServletRequest request, Model model) {
		String orderNo = request.getParameter("orderNo");
		List<WmOutboundHeaderQueryItem> list = outboundHeaderService.selectByKey(orderNo);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new WmOutboundHeaderQueryItem();
	}

	@RequestMapping("/allocByOrderNo")
	@ResponseBody
	public Message allocByOrderNo(@RequestParam("orderNo") String orderNo) {
		return outboundDetailService.allocByOrderNo(orderNo);
	}

	@RequestMapping("/cancelAllocByOrderNo")
	@ResponseBody
	public Message cancelAllocByOrderNo(@RequestParam("orderNo") String orderNo) {
		return outboundDetailService.cancelAllocByOrderNo(orderNo);
	}

	@RequestMapping("/alloc")
	@ResponseBody
	public Message alloc(@RequestParam("orderNo") String orderNo, @RequestParam("lineNos") String[] lineNos,
			@RequestParam("type") String type) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		for (String lineNo : lineNos) {
			try {
				Message singleMessage = outboundDetailService.allocByKey(orderNo, lineNo, type);
				if (singleMessage.getCode() != 200) {
					errors.add(singleMessage.getMsg());
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errors.add(e.getMessage());
			}
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
			message.converMsgsToMsg("");
		}
		return message;
	}

	@RequestMapping("/cancelAlloc")
	@ResponseBody
	public Message cancelAlloc(@RequestParam("orderNo") String orderNo, @RequestParam("lineNos") String[] lineNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		for (String lineNo : lineNos) {
			try {
				Message singleMessage = outboundDetailService.cancelAllocByKey(orderNo, lineNo);
				if (singleMessage.getCode() != 200) {
					errors.add(singleMessage.getMsg());
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errors.add(e.getMessage());
			}
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
			message.converMsgsToMsg("");
		}
		return message;
	}

	@RequestMapping("/audit")
	@ResponseBody
	public Message audit(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			for (String orderNo : orderNos) {
				queryItem = outboundHeaderService.audit(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderNos.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	@RequestMapping("/cancelAudit")
	@ResponseBody
	public Message cancelAudit(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			for (String orderNo : orderNos) {
				queryItem = outboundHeaderService.cancelAudit(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderNos.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	@RequestMapping("/pickByAlloc")
	@ResponseBody
	public Message pickByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		String pickNumStr = request.getParameter("pickNum");
		double pickNum = Double.parseDouble(pickNumStr);
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.pickByAlloc(bean, pickNum);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/cancelPickByAlloc")
	@ResponseBody
	public Message cancelPickByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.cancelPickByAlloc(bean);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/shipByAlloc")
	@ResponseBody
	public Message shipByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.shipByAlloc(bean, true);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/cancelShipByAlloc")
	@ResponseBody
	public Message cancelShipByAlloc(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.cancelShipByAlloc(bean, true);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/shipByHeader")
	@ResponseBody
	public Message shipByHeader(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		try {
			message = this.outboundAllocService.shipByHeader(orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		return message;
	}

	@RequestMapping("/cancelShipByHeader")
	@ResponseBody
	public Message cancelShipByHeader(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		try {
			message = this.outboundAllocService.cancelShipByHeader(orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		return message;
	}

	@RequestMapping("/pickByHeader")
	@ResponseBody
	public Message pickByHeader(HttpServletRequest request, Model model) {
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		try {
			message = this.outboundAllocService.pickByOrderNo(orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		return message;
	}

	@RequestMapping("/queryHistoryPrice")
	@ResponseBody
	public PageEntity<WmOutboundDetailPriceQueryItem> queryHistoryPrice(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<WmOutboundDetailPriceQueryItem> pageEntity = new PageEntity<WmOutboundDetailPriceQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmOutboundDetailPriceQueryItem> list = outboundDetailService.queryHistoryPrice(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/queryHistorySale")
	@ResponseBody
	public PageEntity<WmOutboundDetailSaleHistoryQueryItem> queryHistorySale(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<WmOutboundDetailSaleHistoryQueryItem> pageEntity = new PageEntity<WmOutboundDetailSaleHistoryQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmOutboundDetailSaleHistoryQueryItem> list = outboundDetailService.queryHistorySale(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/createVoucher")
	@ResponseBody
	public Message createVoucher(@RequestParam("ids") String ids, @RequestParam("period") String period,
			@RequestParam("type") String type) {
		return utVoucherService.createVoucherByOutbound(ids.split(","), period, type);
	}

	@RequestMapping("/accountByOrderNo")
	@ResponseBody
	public Message accountByOrderNo(HttpServletRequest request, Model model) {
		String orderNo = request.getParameter("orderNo");
		Message message = new Message();
		try {
			message = outboundHeaderService.accountByOrderNo(orderNo);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/accountByOrderNos")
	@ResponseBody
	public Message accountByOrderNos(@RequestParam("orderNos") String orderNos,
			@RequestParam("outboundType") String outboundType) {
		List<String> orderNoList = new ArrayList<String>();
		String[] orderArray = orderNos.split(",");
		for (String orderNo : orderArray) {
			orderNoList.add(orderNo);
		}
		Message message = new Message();
		try {
			message = outboundHeaderService.accountByOrderNos(orderNoList, outboundType);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/accountCostByOrderNo")
	@ResponseBody
	public Message accountCostByOrderNo(HttpServletRequest request, Model model) {
		String orderNo = request.getParameter("orderNo");
		Message message = new Message();
		try {
			message = outboundHeaderService.accountForCostByOrderNo(orderNo);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/accountCostByOrderNos")
	@ResponseBody
	public Message accountCostByOrderNos(@RequestParam("orderNos") String orderNos,
			@RequestParam("outboundType") String outboundType) {
		List<String> orderNoList = new ArrayList<String>();
		String[] orderArray = orderNos.split(",");
		for (String orderNo : orderArray) {
			orderNoList.add(orderNo);
		}
		Message message = new Message();
		try {
			message = outboundHeaderService.accountForCostByOrderNos(orderNoList, outboundType);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/aiForOrders")
	@ResponseBody
	public Message aiForOrders(HttpServletRequest request, Model model) {

		Message message = new Message();
		String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
		AipOcr client = new AipOcr("15621731", "cQQezpqmic8ufarsd6OweZOB", "860k5mUIRujlIjUpkM3nRG3HIeOpqdo2");
		// 可选：设置网络连接参数
		// client.setConnectionTimeoutInMillis(2000);
		// client.setSocketTimeoutInMillis(60000);
		String path = "D:\\4.jpg";
		org.json.JSONObject res = client.handwriting(path, new HashMap<String, String>());
		System.out.println(res.toString(2));
		// try {
		// String sign = TXCloudSign.appSign(1253892890,
		// "AKIDM0g7Rc7JK72zHScxvjJi5C5om6nJELH3",
		// "geNsbAUdcMekbhCF3cw5k7PSnO4VFyfB", "", 86400);
		// System.out.println(sign);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return null;
	}

	@RequestMapping(value = "/uploadOrderPic", consumes = "multipart/form-data", method = RequestMethod.POST)
	@ResponseBody
	public Message uploadOrderPic(HttpServletRequest request, @RequestParam("file") MultipartFile pic) {
		String webPicUploadUrl = (String) CustomizedPropertyConfigurer.getContextProperty("webPicUploadUrl");
		Message message = new Message();
		// String realPath =
		// request.getSession().getServletContext().getRealPath("/WEB-INF/upload/FittingSkuPic/"+fittingSkuCode+"-"+userDetails.getCompanyId());
		String realPath = webPicUploadUrl + "/outboundOrderPic/";
		System.out.println("图片上传路径：" + realPath);
		try {
			String fileName = System.currentTimeMillis() + "";
			File originFile = new File(realPath, fileName + ".jpg");
			FileUtils.copyInputStreamToFile(pic.getInputStream(), originFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg("文件[" + pic.getOriginalFilename() + "]上传失败!");
			return message;
		}
		message.setCode(200);
		return message;
	}

	@RequestMapping(value = "/importOutboundDetailByExcel", consumes = "multipart/form-data", method = RequestMethod.POST)
	@ResponseBody
	public Message importOutboundDetailByExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		String orderNo = request.getParameter("orderNo");
		String loc = request.getParameter("loc");
		String skuCodeColumnName = request.getParameter("skuCodeColumnName");
		String priceColumnName = request.getParameter("priceColumnName");
		String numColumnName = request.getParameter("numColumnName");
		try {
			return outboundDetailService.importByExcel(file, orderNo, loc, skuCodeColumnName, priceColumnName,
					numColumnName);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/queryWmOutboundDetailByPage")
	@ResponseBody
	public PageEntity<WmOutboundDetailSkuQueryItem> queryWmOutboundDetailByPage(HttpServletRequest request,
			Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmOutboundDetailSkuQueryItem> pageEntity = new PageEntity<WmOutboundDetailSkuQueryItem>();
		Page<?> page = new Page();
		Map map = new HashMap<>();
		// 配置分页参数
		if (request.getParameter("page") != null && request.getParameter("size") != null) {
			page.setPageNo(Integer.parseInt(request.getParameter("page")));
			page.setPageSize(Integer.parseInt(request.getParameter("size")));
			map = JSONObject.parseObject(request.getParameter("conditions"));
			map.put("page", page);

		} else {
			map = JSONObject.parseObject(request.getParameter("conditions"));
		}
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmOutboundDetailSkuQueryItem> list = outboundDetailService.queryWmOutboundDetailByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}
}
