package com.xibin.fin.controller;

import java.util.HashMap;
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
import com.xibin.fin.entity.FiCourseBalanceGLQueryItem;
import com.xibin.fin.entity.FiVoucherDetailQueryItem;
import com.xibin.fin.entity.FiVoucherEntity;
import com.xibin.fin.entity.FiVoucherGLEntity;
import com.xibin.fin.entity.FiVoucherSumByCourseQueryItem;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;
import com.xibin.fin.service.FiCourseService;
import com.xibin.fin.service.FiVoucherDetailService;
import com.xibin.fin.service.FiVoucherGLService;
import com.xibin.fin.service.FiVoucherService;


@Controller
@RequestMapping(value = "/accountBook", produces = {"application/json;charset=UTF-8"})
public class AccountBookController {
	@Resource
	private FiVoucherGLService fiVoucherGLService;

	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/showVoucherGL")
	@ResponseBody
	public PageEntity<FiVoucherGLEntity> showVoucherGL(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<FiVoucherGLEntity> pageEntity = new PageEntity<FiVoucherGLEntity>();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		//map.put("page", page);
		List<FiVoucherGLEntity> list = fiVoucherGLService.queryForVoucherGL(map);
		pageEntity.setList(list);
		pageEntity.setSize((long)list.size());
	    return  pageEntity;
	}
	
	@RequestMapping("/showAccountBalance")
	@ResponseBody
	public PageEntity<FiCourseBalanceGLQueryItem> showAccountBalance(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<FiCourseBalanceGLQueryItem> pageEntity = new PageEntity<FiCourseBalanceGLQueryItem>();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		
		List<FiCourseBalanceGLQueryItem> list = fiVoucherGLService.queryForAccountBalance(map);
		pageEntity.setList(list);
		pageEntity.setSize((long)list.size());
	    return  pageEntity;
	}
	@RequestMapping("/showVoucherDetailSum")
	@ResponseBody
	public PageEntity<FiVoucherSumByCourseQueryItem> showVoucherDetailSum(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<FiVoucherSumByCourseQueryItem> pageEntity = new PageEntity<FiVoucherSumByCourseQueryItem>();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		
		List<FiVoucherSumByCourseQueryItem> list = fiVoucherGLService.queryForVoucherDetailSum(map);
		pageEntity.setList(list);
		pageEntity.setSize((long)list.size());
	    return  pageEntity;
	}
	@RequestMapping("/showVoucherCount")
	@ResponseBody
	public Integer showForVoucherDetailSum(HttpServletRequest request,Model model){ 

		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		
	
	    return  fiVoucherGLService.queryForVoucherCount(map);
	}
}
