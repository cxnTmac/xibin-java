package com.xibin.wms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.swing.internal.plaf.metal.resources.metal_zh_TW;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.service.BdFittingSkuPicService;
import com.xibin.wms.service.BdFittingSkuService;
import com.xibin.wms.service.BdFittingTypeService;
import com.xibin.wms.service.WmInboundHeaderService;

@Controller
@RequestMapping(value = "/inbound", produces = {"application/json;charset=UTF-8"})
public class InboundController {
	@Resource
	private WmInboundHeaderService inboundHeaderService;
	
	@RequestMapping("/showAllInboundOrder")
	@ResponseBody
	public PageEntity<WmInboundHeader> showAllInboundOrder(HttpServletRequest request,Model model){ 
	    // 开始分页  
		PageEntity<WmInboundHeader> pageEntity = new PageEntity<WmInboundHeader>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<WmInboundHeader> list = inboundHeaderService.getAllInboundOrderByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	 
	  
}
