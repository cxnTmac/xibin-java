package com.xibin.wms.controller;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.costants.Constants;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.pojo.BdModel;
import com.xibin.wms.query.WmInventoryQueryItem;
import com.xibin.wms.service.BdFittingTypeService;
import com.xibin.wms.service.BdModelService;
import com.xibin.wms.service.WmInventoryService;

@Controller
@RequestMapping(value = "/inventory", produces = {"application/json;charset=UTF-8"})
public class InventoryController {
	@Resource
	private WmInventoryService wmInventoryService;
	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/showAllInventory")
	@ResponseBody
	public PageEntity<WmInventoryQueryItem> showAllInventory(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmInventoryQueryItem> pageEntity = new PageEntity<WmInventoryQueryItem>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
			map.put("companyId", userDetails.getWarehouseId());
		}
		List<WmInventoryQueryItem> userList = wmInventoryService.getAllInventoryByPage(map);
		pageEntity.setList(userList);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	
	  
}
