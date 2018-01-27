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
import com.xibin.wms.query.WmActTranQueryItem;
import com.xibin.wms.query.WmInventoryQueryItem;
import com.xibin.wms.service.BdFittingTypeService;
import com.xibin.wms.service.BdModelService;
import com.xibin.wms.service.WmActTranService;
import com.xibin.wms.service.WmInventoryService;

@Controller
@RequestMapping(value = "/actTran", produces = {"application/json;charset=UTF-8"})
public class ActTranController {
	@Resource
	private WmActTranService wmActTranService;
	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/showAllActTran")
	@ResponseBody
	public PageEntity<WmActTranQueryItem> showAllActTran(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmActTranQueryItem> pageEntity = new PageEntity<WmActTranQueryItem>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmActTranQueryItem> userList = wmActTranService.getAllActTranByPage(map);
		pageEntity.setList(userList);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	
	  
}
