package com.xibin.wms.controller;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.costants.Constants;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.fin.service.FiCourseAuxiliaryBalanceService;
import com.xibin.wms.pojo.BdCustomer;
import com.xibin.wms.query.BdCustomerQueryItem;
import com.xibin.wms.service.BdCustomerCopyService;
import com.xibin.wms.service.BdCustomerService;

@Controller
@RequestMapping(value = "/customer", produces = { "application/json;charset=UTF-8" })
public class CustomerController {
	@Resource
	private BdCustomerService bdCustomerService;
	@Resource
	private BdCustomerCopyService bdCustomerCopyService;
	@Autowired
	private HttpSession session;
	@Autowired
	private FiCourseAuxiliaryBalanceService fiCourseAuxiliaryBalanceService;

	@RequestMapping("/showAllCustomer")
	@ResponseBody
	public PageEntity<BdCustomerQueryItem> showAllCustomer(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdCustomerQueryItem> pageEntity = new PageEntity<BdCustomerQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<BdCustomerQueryItem> list = bdCustomerService.getAllCustomerByPage(map);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
		}
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/getCustomerByZoneCode")
	@ResponseBody
	public BdCustomerQueryItem getCustomerByZoneCode(HttpServletRequest request, Model model) {
		String customerCode = request.getParameter("customerCode");
		List<BdCustomerQueryItem> list = bdCustomerService.selectByKey(customerCode);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new BdCustomerQueryItem();
	}

	@RequestMapping("/removeCustomer")
	@ResponseBody
	public Message removeCustomer(HttpServletRequest request, Model model) {
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String customerCode = request.getParameter("customerCode");
		try {
			this.bdCustomerService.removeCustomer(id, customerCode);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setMsg(e.getMessage());
			message.setCode(0);
			return message;
		}
		message.setCode(200);
		message.setMsg("删除成功");
		return message;
	}

	@RequestMapping("/batchRemove")
	@ResponseBody
	public Message batchRemove(@RequestParam("ids") int[] ids, @RequestParam("customerCodes") String[] customerCodes) {
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			try {
				this.bdCustomerService.removeCustomer(ids[i], customerCodes[i]);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stringBuffer.append(e.getMessage() + "</br>");
			}
		}
		if (stringBuffer.length() > 0) {
			message.setMsg(stringBuffer.toString());
			message.setCode(0);
			return message;
		}
		message.setCode(200);
		message.setMsg("全部删除成功");
		return message;
	}

	// @RequestMapping("/saveCustomer")
	// @ResponseBody
	// public Message saveCustomer(HttpServletRequest request,Model model){
	// Message message = new Message();
	// String str = request.getParameter("customer");
	// BdCustomer bean = JSON.parseObject(str, BdCustomer.class);
	// BdCustomer result = new BdCustomer();
	// try {
	// result = this.bdCustomerService.saveCustomer(bean);
	// message.setCode(200);
	// message.setData(result);
	// message.setMsg("操作成功！");
	// } catch (BusinessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// message.setCode(0);
	// message.setMsg(e.getMessage());
	// }
	// return message;
	// }
	@RequestMapping("/saveCustomer")
	@ResponseBody
	public Message saveCustomer(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("customer");
		BdCustomer bean = JSON.parseObject(str, BdCustomer.class);
		BdCustomer result = new BdCustomer();
		try {
			result = this.bdCustomerService.saveCustomer(bean);
			message.setCode(200);
			message.setData(result);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/addCustomerCourseAuxiliaryBlance")
	@ResponseBody
	public Message addCustomerCourseAuxiliaryBlance(HttpServletRequest request, Model model) {
		String customerCode = request.getParameter("customerCode");
		String balance = request.getParameter("balance");
		String courseNo = request.getParameter("courseNo");
		double courseBalance = Double.parseDouble(balance);
		return fiCourseAuxiliaryBalanceService.addCustomerCourseAuxiliaryBlance(customerCode, courseBalance, courseNo);
	}
}
