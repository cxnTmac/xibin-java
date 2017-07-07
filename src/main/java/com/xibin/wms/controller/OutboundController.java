package com.xibin.wms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmOutboundAlloc;
import com.xibin.wms.pojo.WmOutboundDetail;
import com.xibin.wms.pojo.WmOutboundHeader;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmOutboundAllocQueryItem;
import com.xibin.wms.query.WmOutboundDetailQueryItem;
import com.xibin.wms.query.WmOutboundHeaderQueryItem;
import com.xibin.wms.service.WmOutboundDetailService;
import com.xibin.wms.service.WmOutboundHeaderService;
import com.xibin.wms.service.WmOutboundAllocService;

@Controller
@RequestMapping(value = "/outbound", produces = {"application/json;charset=UTF-8"})
public class OutboundController {
	@Resource
	private WmOutboundHeaderService outboundHeaderService;
	@Resource
	private WmOutboundDetailService outboundDetailService;
	@Resource
	private WmOutboundAllocService outboundAllocService;
	@RequestMapping("/showAllOutboundOrder")
	@ResponseBody
	public PageEntity<WmOutboundHeaderQueryItem> showAllOutboundOrder(HttpServletRequest request,Model model){ 
	    // 开始分页  
		PageEntity<WmOutboundHeaderQueryItem> pageEntity = new PageEntity<WmOutboundHeaderQueryItem>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmOutboundHeaderQueryItem> list = outboundHeaderService.getAllOutboundOrderByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	@RequestMapping("/showAllOutboundDetail")
	@ResponseBody
	public PageEntity<WmOutboundDetailQueryItem> showAllOutboundDetail(HttpServletRequest request,Model model){ 
	    // 开始分页  
		PageEntity<WmOutboundDetailQueryItem> pageEntity = new PageEntity<WmOutboundDetailQueryItem>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmOutboundDetailQueryItem> list = outboundDetailService.getAllOutboundDetailByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	@RequestMapping("/showAllOutboundAlloc")
	@ResponseBody
	public PageEntity<WmOutboundAllocQueryItem> showAllOutboundAlloc(HttpServletRequest request,Model model){ 
	    // 开始分页  
		PageEntity<WmOutboundAllocQueryItem> pageEntity = new PageEntity<WmOutboundAllocQueryItem>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmOutboundAllocQueryItem> list = outboundAllocService.getAllOutboundAllocByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	@RequestMapping("/saveOutboundOrder")
	@ResponseBody
	public Message  saveOutboundOrder(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String str = request.getParameter("order");
		WmOutboundHeader bean = JSON.parseObject(str, WmOutboundHeader.class);
		try {
			WmOutboundHeaderQueryItem queryItem = this.outboundHeaderService.saveOutbound(bean);
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
	@RequestMapping("/remove")
	@ResponseBody
	public Message  remove(@RequestParam("orderNos") String [] orderNos){ 
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		try {
			for(String orderNo:orderNos){
				outboundHeaderService.remove(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if(errors.size()==0){
			message.setCode(200);
			message.setMsg("操作成功！");
		}else{
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}
	@RequestMapping("/removeOutboundDetail")
	@ResponseBody
	public Message  removeOutboundDetail(@RequestParam("ids") int [] ids,@RequestParam("orderNo") String  orderNo){ 
		Message message = new Message();
		try {
			this.outboundDetailService.removeOutboundDetail(ids, orderNo);
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
	@RequestMapping("/saveOutboundDetail")
	@ResponseBody
	public Message  saveOutboundDetail(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String str = request.getParameter("detail");
		WmOutboundDetail bean = JSON.parseObject(str, WmOutboundDetail.class);
		try {
			this.outboundDetailService.saveOutboundDetail(bean);
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
	@RequestMapping("/getOutboundHeaderByOderNo")
	@ResponseBody
	public WmOutboundHeaderQueryItem getOutboundHeaderByOderNo(HttpServletRequest request,Model model){
		String orderNo = request.getParameter("orderNo");
		List<WmOutboundHeaderQueryItem> list = outboundHeaderService.selectByKey(orderNo);
		if(!list.isEmpty()){
			return list.get(0);
		}
		return new WmOutboundHeaderQueryItem();
	}
	

	
	
	
	@RequestMapping("/alloc")
	@ResponseBody
	public Message  alloc(@RequestParam("orderNo")String orderNo, @RequestParam("lineNos") String [] lineNos){ 
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		for(String lineNo:lineNos){
			try {
				Message singleMessage = outboundDetailService.allocByKey(orderNo, lineNo);
				if(singleMessage.getCode()!=200){
					errors.add(singleMessage.getMsg());
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errors.add(e.getMessage());
			}
		}
		if(errors.size() == 0){
			message.setCode(200);
			message.setMsg("操作成功");
		}else{
			message.setCode(100);
			message.setMsgs(errors);
			message.converMsgsToMsg("");
		}
		return message;
	}
	@RequestMapping("/cancelAlloc")
	@ResponseBody
	public Message  cancelAlloc(@RequestParam("orderNo")String orderNo, @RequestParam("lineNos") String [] lineNos){ 
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		for(String lineNo:lineNos){
			try {
				Message singleMessage = outboundDetailService.cancelAllocByKey(orderNo, lineNo);
				if(singleMessage.getCode()!=200){
					errors.add(singleMessage.getMsg());
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errors.add(e.getMessage());
			}
		}
		if(errors.size() == 0){
			message.setCode(200);
			message.setMsg("操作成功");
		}else{
			message.setCode(100);
			message.setMsgs(errors);
			message.converMsgsToMsg("");
		}
		return message;
	}
	 
	@RequestMapping("/audit")
	@ResponseBody
	public Message  audit(@RequestParam("orderNos") String [] orderNos){ 
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			for(String orderNo:orderNos){
				queryItem = outboundHeaderService.audit(orderNo);
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
		WmOutboundHeaderQueryItem queryItem = new WmOutboundHeaderQueryItem();
		try {
			for(String orderNo:orderNos){
				queryItem = outboundHeaderService.cancelAudit(orderNo);
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
	@RequestMapping("/pickByAlloc")
	@ResponseBody
	public Message  pickByAlloc(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.pickByAlloc(bean);
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
	@RequestMapping("/cancelPickByAlloc")
	@ResponseBody
	public Message  cancelPickByAlloc(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.cancelPickByAlloc(bean);
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
	@RequestMapping("/shipByAlloc")
	@ResponseBody
	public Message  shipByAlloc(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.shipByAlloc(bean,true);
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
	
	@RequestMapping("/cancelShipByAlloc")
	@ResponseBody
	public Message  cancelShipByAlloc(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String str = request.getParameter("alloc");
		WmOutboundAlloc bean = JSON.parseObject(str, WmOutboundAlloc.class);
		try {
			this.outboundAllocService.cancelShipByAlloc(bean,true);
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
	
	@RequestMapping("/shipByHeader")
	@ResponseBody
	public Message  shipByHeader(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		try {
			message = this.outboundAllocService.shipByHeader(orderNo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
		return message;
	}
	
	@RequestMapping("/cancelShipByHeader")
	@ResponseBody
	public Message  cancelShipByHeader(HttpServletRequest request,Model model){ 
		Message message = new Message();
		String orderNo = request.getParameter("orderNo");
		try {
			message = this.outboundAllocService.cancelShipByHeader(orderNo);
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
