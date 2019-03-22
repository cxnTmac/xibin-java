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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.costants.Constants;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.pojo.BdSkuAnotherName;
import com.xibin.wms.service.BdSkuAnotherNameService;

@Controller
@RequestMapping(value = "/anotherName", produces = { "application/json;charset=UTF-8" })
public class SkuAnotherNameController {
	@Resource
	private BdSkuAnotherNameService bdSkuAnotherNameService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllAnotherName")
	@ResponseBody
	public PageEntity<BdSkuAnotherName> showAllAnotherName(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdSkuAnotherName> pageEntity = new PageEntity<BdSkuAnotherName>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BdSkuAnotherName> list = bdSkuAnotherNameService.getAllAnotherNameByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/getAnotherNameBySkuCodeAndCustomerCode")
	@ResponseBody
	public BdSkuAnotherName getAnotherNameBySkuCodeAndCustomerCode(HttpServletRequest request, Model model) {
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		String customerCode = request.getParameter("customerCode");
		List<BdSkuAnotherName> list = bdSkuAnotherNameService.selectByKey(fittingSkuCode, customerCode);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new BdSkuAnotherName();
	}

	@RequestMapping("/removeAnotherName")
	@ResponseBody
	public Message removeAnotherName(HttpServletRequest request, Model model) {
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		String customerCode = request.getParameter("customerCode");
		try {
			this.bdSkuAnotherNameService.removeAnotherName(id, fittingSkuCode, customerCode);
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

	@RequestMapping("/saveAnotherName")
	@ResponseBody
	public Message saveAnotherName(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("anotherName");
		BdSkuAnotherName bean = JSON.parseObject(str, BdSkuAnotherName.class);
		BdSkuAnotherName result = new BdSkuAnotherName();
		try {
			result = this.bdSkuAnotherNameService.saveAnotherName(bean);
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

	@RequestMapping(value = "/importSkuCodesByExcel", consumes = "multipart/form-data", method = RequestMethod.POST)
	@ResponseBody
	public Message importSkuCodesByExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		String customerCode = request.getParameter("customerCode");
		String skuCodeColumnName = request.getParameter("skuCodeColumnName");
		try {
			return bdSkuAnotherNameService.importByExcel(file, customerCode, skuCodeColumnName);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping(value = "/importSkuCodesByExcelForSave", consumes = "multipart/form-data", method = RequestMethod.POST)
	@ResponseBody
	public Message importSkuCodesByExcelForSave(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		String customerCode = request.getParameter("customerCode");
		String skuCodeColumnName = request.getParameter("skuCodeColumnName");
		String customerSkuCodeColumnName = request.getParameter("customerSkuCodeColumnName");
		String remark = request.getParameter("remark");
		try {
			return bdSkuAnotherNameService.importByExcelForSave(file, customerCode, skuCodeColumnName,
					customerSkuCodeColumnName, remark);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}
}
