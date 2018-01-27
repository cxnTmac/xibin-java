package com.xibin.fin.controller;

import java.util.Date;
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
import com.xibin.fin.entity.FiVoucherDetailQueryItem;
import com.xibin.fin.entity.FiVoucherEntity;
import com.xibin.fin.pojo.FiCourse;
import com.xibin.fin.pojo.FiVoucher;
import com.xibin.fin.pojo.FiVoucherDetail;
import com.xibin.fin.service.FiCourseService;
import com.xibin.fin.service.FiTermService;
import com.xibin.fin.service.FiVoucherDetailService;
import com.xibin.fin.service.FiVoucherService;


@Controller
@RequestMapping(value = "/term", produces = {"application/json;charset=UTF-8"})
public class TermController {

	@Resource
	private FiTermService fiTermService;
	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/lossAndGainBroughtForward")
	@ResponseBody
	public Message lossAndGainBroughtForward(HttpServletRequest request,Model model){ 
	    // 开始分页  
		String date = request.getParameter("date");
		String summary = request.getParameter("summary");
		String voucherWord = request.getParameter("voucherWord");
	    try {
			return  fiTermService.lossAndGainBroughtForward(new Date(),summary,voucherWord);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			e.printStackTrace();
			return message;
		}
	    
	}
	
	
	@RequestMapping("/endTerm")
	@ResponseBody
	public Message endTerm(HttpServletRequest request,Model model){ 
	    // 开始分页  
	    try {
			return  fiTermService.endTerm();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			e.printStackTrace();
			return message;
		}
	}
	
	
	  
}
