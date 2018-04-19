package com.xibin.fin.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.xibin.core.costants.Constants;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.fin.entity.FiVoucherDetailQueryItem;
import com.xibin.fin.entity.FiVoucherEntity;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;
import com.xibin.fin.service.FiVoucherDetailService;
import com.xibin.fin.service.FiVoucherService;

@Controller
@RequestMapping(value = "/voucher", produces = { "application/json;charset=UTF-8" })
public class VoucherController {
	@Resource
	private FiVoucherService fiVoucherService;
	@Resource
	private FiVoucherDetailService fiVoucherDetailService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllVoucher")
	@ResponseBody
	public PageEntity<FiVoucherEntity> showAllVoucher(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<FiVoucherEntity> pageEntity = new PageEntity<FiVoucherEntity>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		// map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("bookId", userDetails.getBookId());
		}
		List<FiVoucherEntity> list = fiVoucherDetailService.getAllVoucherEntityByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize((long) list.size());
		return pageEntity;
	}

	@RequestMapping("/getVoucherById")
	@ResponseBody
	public FiVoucher getVoucherById(@RequestParam("id") int id) {
		return fiVoucherService.getVoucherById(id);

	}

	@RequestMapping("/checkVoucher")
	@ResponseBody
	public Message checkVoucher(HttpServletRequest request, Model model) {
		Message message = new Message();
		FiVoucher voucher = JSONObject.parseObject(request.getParameter("voucher"), FiVoucher.class);
		try {
			message = fiVoucherService.checkVoucher(voucher);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}

	}

	@RequestMapping("/removeVoucher")
	@ResponseBody
	public Message removeVoucher(@RequestParam("id") int id) {
		Message message = new Message();
		return fiVoucherService.removeVoucher(id);
	}

	@RequestMapping("/cancelCheckVoucher")
	@ResponseBody
	public Message cancelCheckVoucher(HttpServletRequest request, Model model) {
		Message message = new Message();
		FiVoucher voucher = JSONObject.parseObject(request.getParameter("voucher"), FiVoucher.class);
		try {
			message = fiVoucherService.cancelCheckVoucher(voucher);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}

	}

	@RequestMapping("/getVoucherDetailsByVoucherId")
	@ResponseBody
	public PageEntity<FiVoucherDetailQueryItem> getVoucherDetailsByVoucherId(@RequestParam("voucherId") int voucherId) {
		// 开始分页

		PageEntity<FiVoucherDetailQueryItem> pageEntity = new PageEntity<FiVoucherDetailQueryItem>();
		// map.put("page", page);

		List<FiVoucherDetailQueryItem> list = fiVoucherDetailService.selectByVoucherIdForQueryItem(voucherId + "");
		pageEntity.setList(list);
		pageEntity.setSize((long) list.size());
		return pageEntity;
	}

	@RequestMapping("/saveVoucherAndDetail")
	@ResponseBody
	public Message saveVoucherAndDetail(HttpServletRequest request, Model model) {
		Message message = new Message();
		FiVoucher voucher = JSONObject.parseObject(request.getParameter("voucher"), FiVoucher.class);
		List<FiVoucherDetail> details = JSONObject.parseArray(request.getParameter("details"), FiVoucherDetail.class);
		List<FiVoucherDetail> removeDetails = JSONObject.parseArray(request.getParameter("removeDetails"),
				FiVoucherDetail.class);
		try {
			message = fiVoucherDetailService.saveVoucherAndDetail(voucher, details, removeDetails);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		return message;
	}

}
