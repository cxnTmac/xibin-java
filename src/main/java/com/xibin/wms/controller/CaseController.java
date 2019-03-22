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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.costants.Constants;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.crm.pojo.CrmCase;
import com.xibin.crm.service.CrmCaseService;

@Controller
@RequestMapping(value = "/case", produces = { "application/json;charset=UTF-8" })
public class CaseController {
	@Resource
	private CrmCaseService crmCaseService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllCase")
	@ResponseBody
	public PageEntity<CrmCase> showAllCase(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<CrmCase> pageEntity = new PageEntity<CrmCase>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<CrmCase> list = crmCaseService.getAllCaseByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/getCaseByStuNum")
	@ResponseBody
	public CrmCase getCaseByStuNum(HttpServletRequest request, Model model) {
		String stuNum = request.getParameter("stuNum");
		List<CrmCase> list = crmCaseService.selectByKey(stuNum);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new CrmCase();
	}

	@RequestMapping("/removeCase")
	@ResponseBody
	public Message removeCase(HttpServletRequest request, Model model) {
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String stuNum = request.getParameter("stuNum");
		try {
			this.crmCaseService.removeCase(id, stuNum);
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

	@RequestMapping("/saveCase")
	@ResponseBody
	public Message saveCase(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("case");
		CrmCase bean = JSON.parseObject(str, CrmCase.class);
		CrmCase result = new CrmCase();
		try {
			result = this.crmCaseService.saveCase(bean);
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

}
