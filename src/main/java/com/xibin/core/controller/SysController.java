package com.xibin.core.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.SysCompany;
import com.xibin.wms.pojo.SysFunction;
import com.xibin.wms.pojo.SysRole;
import com.xibin.wms.service.SysCompanyService;
import com.xibin.wms.service.SysFunctionService;
import com.xibin.wms.service.SysRoleService;

@Controller
@RequestMapping(value = "/sys", produces = { "application/json;charset=UTF-8" })
public class SysController {
	@Autowired
	private HttpSession session;
	@Autowired
	private SysCompanyService sysCompanyService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysFunctionService sysFunctionService;

	@RequestMapping("/showAllCompanyListPage")
	@ResponseBody
	public PageEntity<SysCompany> showAllCompanyListPage(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<SysCompany> pageEntity = new PageEntity<SysCompany>();
		Page page = new Page();
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<SysCompany> userList = sysCompanyService.getAllCompanyByPage(map);
		pageEntity.setList(userList);
		// pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllRoleListPage")
	@ResponseBody
	public PageEntity<SysRole> showAllRoleListPage(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<SysRole> pageEntity = new PageEntity<SysRole>();
		Page page = new Page();
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<SysRole> userList = sysRoleService.getAllRoleByPage(map);
		pageEntity.setList(userList);
		// pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllFunctionListPage")
	@ResponseBody
	public PageEntity<SysFunction> showAllFunctionListPage(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<SysFunction> pageEntity = new PageEntity<SysFunction>();
		Page page = new Page();
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<SysFunction> userList = sysFunctionService.getAllFunctionByPage(map);
		pageEntity.setList(userList);
		// pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllMenus")
	@ResponseBody
	public List<SysFunction> showAllMenus(HttpServletRequest request, Model model) {
		return sysFunctionService.selectAllMenus();
	}

	@RequestMapping("/getAllRoleFunctions")
	@ResponseBody
	public Message getAllRoleFunctions(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = "";
		message.setCode(200);
		return message;
	}

	@RequestMapping("/showAllMenu")
	@ResponseBody
	public List<SysFunction> showAllMenu(HttpServletRequest request, Model model) {
		return sysFunctionService.selectAllMenus();
	}

}
