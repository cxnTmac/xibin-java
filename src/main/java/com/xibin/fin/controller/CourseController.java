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
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.service.FiCourseService;

@Controller
@RequestMapping(value = "/course", produces = { "application/json;charset=UTF-8" })
public class CourseController {
	@Resource
	private FiCourseService fiCourseService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllCourse")
	@ResponseBody
	public PageEntity<FiCourse> showAllCourse(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<FiCourse> pageEntity = new PageEntity<FiCourse>();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("bookId", userDetails.getBookId());
		}
		List<FiCourse> list = fiCourseService.getAllCourseByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize((long) list.size());
		return pageEntity;
	}

	@RequestMapping("/saveCourseBalance")
	@ResponseBody
	public Message saveVoucherAndDetail(@RequestParam("courses") String courses) {
		Message message = new Message();
		List<FiCourse> a = JSONObject.parseArray(courses, FiCourse.class);
		try {
			fiCourseService.saveCourseBalance(a);

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		message.setCode(200);
		message.setMsg("保存成功");
		return message;
	}

}
