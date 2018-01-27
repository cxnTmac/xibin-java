package com.xibin.wms.controller;

import java.util.ArrayList;
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
import com.xibin.wms.pojo.BdLoc;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.pojo.WmAssembleFDetail;
import com.xibin.wms.pojo.WmAssembleHeader;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.BdLocQueryItem;
import com.xibin.wms.query.BdZoneQueryItem;
import com.xibin.wms.query.WmAssembleFDetailQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.service.BdLocService;
import com.xibin.wms.service.BdZoneService;
import com.xibin.wms.service.WmAssembleFDetailService;
import com.xibin.wms.service.WmAssembleHeaderService;

@Controller
@RequestMapping(value = "/assemble", produces = {"application/json;charset=UTF-8"})
public class AssembleController {
	@Resource
	private WmAssembleHeaderService wmAssembleHeaderService;
	@Resource
	private WmAssembleFDetailService wmAssembleFDetailService;
	@RequestMapping("/showAllAssembleOrder")
	@ResponseBody
	public PageEntity<WmAssembleHeader> showAllAssembleOrder(HttpServletRequest request,Model model){ 
	    // 开始分页  
		PageEntity<WmAssembleHeader> pageEntity = new PageEntity<WmAssembleHeader>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmAssembleHeader> list = wmAssembleHeaderService.getAllAssembleOrderByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	}
	
	@RequestMapping("/saveAssembleOrder")
	@ResponseBody
	public Message  saveInboundOrder(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String str = request.getParameter("order");
		WmAssembleHeader bean = JSON.parseObject(str, WmAssembleHeader.class);
		try {
			WmAssembleHeader queryItem = this.wmAssembleHeaderService.saveAssembleOrder(bean);
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
	@RequestMapping("/getAssembleHeaderByOderNo")
	@ResponseBody
	public WmAssembleHeader getAssembleHeaderByOderNo(HttpServletRequest request,Model model){
		String orderNo = request.getParameter("orderNo");
		List<WmAssembleHeader> list = wmAssembleHeaderService.selectByKey(orderNo);
		if(!list.isEmpty()){
			return list.get(0);
		}
		return new WmAssembleHeader();
	}
	
	@RequestMapping("/audit")
	@ResponseBody
	public Message  audit(@RequestParam("orderNos") String [] orderNos){ 
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmAssembleHeader queryItem = new WmAssembleHeader();
		try {
			for(String orderNo:orderNos){
				queryItem = wmAssembleHeaderService.audit(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if(errors.size()==0){
			message.setCode(200);
			message.setMsg("操作成功！");
			if(orderNos.length == 1){
				message.setData(queryItem);
			}
		}else{
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}
	
	@RequestMapping("/cancelAudit")
	@ResponseBody
	public Message  cancelAudit(@RequestParam("orderNos") String [] orderNos){ 
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmAssembleHeader queryItem = new WmAssembleHeader();
		try {
			for(String orderNo:orderNos){
				queryItem = wmAssembleHeaderService.cancelAudit(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if(errors.size()==0){
			message.setCode(200);
			message.setMsg("操作成功！");
			if(orderNos.length == 1){
				message.setData(queryItem);
			}
		}else{
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}
	
	
	@RequestMapping("/showAllAssembleFDetail")
	@ResponseBody
	public PageEntity<WmAssembleFDetailQueryItem> showAllAssembleFDetail(HttpServletRequest request,Model model){ 
	    // 开始分页  
		PageEntity<WmAssembleFDetailQueryItem> pageEntity = new PageEntity<WmAssembleFDetailQueryItem>();
		Page<?> page = new Page();
		
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmAssembleFDetailQueryItem> list = wmAssembleFDetailService.getAllAssembleFDetailByOrderNo(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	}
	
	@RequestMapping("/saveAssembleFDetail")
	@ResponseBody
	public Message  saveAssembleFDetail(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String str = request.getParameter("fDetail");
		WmAssembleFDetail bean = JSON.parseObject(str, WmAssembleFDetail.class);
		try {
			WmAssembleFDetail queryItem = this.wmAssembleFDetailService.saveAssembleFDetail(bean);
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
	
	@RequestMapping("/removeAssembleFDetail")
	@ResponseBody
	public Message  removeAssembleFDetail(@RequestParam("orderNo") String  orderNo,@RequestParam("lineNo") String  lineNo){ 
		Message message = new Message();
		try {
			this.wmAssembleFDetailService.remove(orderNo, lineNo);
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
}
