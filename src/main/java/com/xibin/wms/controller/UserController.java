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
import com.xibin.fin.pojo.FiBook;
import com.xibin.fin.pojo.FiTerm;
import com.xibin.fin.service.FiBookService;
import com.xibin.fin.service.FiTermService;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.query.SysUserQueryItem;
import com.xibin.wms.service.UserService;

@Controller
@RequestMapping(value = "/user", produces = { "application/json;charset=UTF-8" })
public class UserController {
	@Resource
	private UserService userService;

	@Resource
	private FiTermService termService;

	@Resource
	private FiBookService bookService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showUser")
	@ResponseBody
	public SysUser toIndex(HttpServletRequest request, Model model) {
		int userId = Integer.parseInt(request.getParameter("id"));
		return this.userService.getUserById(userId);
	}

	@RequestMapping("/showAllUser")
	@ResponseBody
	public PageEntity<SysUserQueryItem> showAllUser(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<SysUserQueryItem> pageEntity = new PageEntity<SysUserQueryItem>();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);

		Page page = new Page();
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
		}
		List<SysUserQueryItem> userList = userService.getAllUserByPage(map);
		pageEntity.setList(userList);
		// pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/removeUser")
	@ResponseBody
	public int removeUser(HttpServletRequest request, Model model) {
		int userId = Integer.parseInt(request.getParameter("id"));
		return this.userService.removeUser(userId);
	}

	@RequestMapping("/saveUser")
	@ResponseBody
	public Message saveUser(HttpServletRequest request, Model model) {
		Message message = new Message();
		String userStr = request.getParameter("user");
		SysUser user = JSON.parseObject(userStr, SysUser.class);
		SysUser result = new SysUser();
		try {
			result = this.userService.saveUser(user);
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

	@RequestMapping("/login")
	@ResponseBody
	public Message login(@RequestParam Map<String, String> params) {
		// AccessTokenUtil.initAndSetAccessToken();
		Message message = new Message();
		SysUser user = JSON.parseObject(params.get("data"), SysUser.class);
		List<SysUser> list = this.userService.selectByUserNameAndPassword(user.getUserName(), user.getPassword());
		if (list.size() > 0) {
			if (list.get(0).getIsEnable().equals("Y")) {
				UserDetails userDetails = new UserDetails();
				userDetails.setUserName(list.get(0).getUserName());
				userDetails.setCompanyId(list.get(0).getCompanyId());
				userDetails.setWarehouseId(1);
				FiBook defaultBook = getDefaultBook(list.get(0).getCompanyId());
				if (defaultBook != null) {
					userDetails.setBookId(defaultBook.getId());
					userDetails.setBookName(defaultBook.getBookName());
					userDetails.setBeginYear(defaultBook.getBeginYear());
					userDetails.setPeriod(defaultBook.getPeriod());
				}
				userDetails.setCurrentPeriod(getCurrentPeriod(list.get(0).getCompanyId()));
				userDetails.setUserId(list.get(0).getId());
				session.setAttribute(Constants.SESSION_USER_KEY, userDetails);
				message.setCode(200);
				message.setData(userDetails);
				message.setMsg("登陆成功！");
			} else {
				message.setCode(0);
				message.setMsg("用户未启用！");
			}
		} else {
			message.setCode(0);
			message.setMsg("用户名或密码错误！");
		}
		return message;
	}

	private String getCurrentPeriod(Integer companyId) {
		FiTerm term = termService.getCurrentTerm(companyId);
		if (term != null) {
			return term.getPeriod();
		}
		return null;
	}

	private FiBook getDefaultBook(Integer companyId) {
		FiBook example = new FiBook();
		example.setCompanyId(companyId);
		example.setIsDefault("Y");
		List<FiBook> results = bookService.findByExample(example);
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

	@RequestMapping("/logout")
	@ResponseBody
	public Message logout(@RequestParam Map<String, String> params) {
		Message message = new Message();
		session.invalidate();
		message.setCode(200);
		return message;
	}
}