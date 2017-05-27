package com.xibin.wms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.pojo.BdModel;
import com.xibin.wms.service.BdFittingTypeService;
import com.xibin.wms.service.BdModelService;

@Controller
@RequestMapping(value = "/model", produces = {"application/json;charset=UTF-8"})
public class ModelController {
	@Resource
	private BdModelService bdModelService;
	
	@RequestMapping("/showAllModel")
	@ResponseBody
	public PageEntity<BdModel> showAllUser(HttpServletRequest request,Model model){ 
	    // 开始分页  
		PageEntity<BdModel> pageEntity = new PageEntity<BdModel>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = new HashMap<>();
		map.put("page", page);
		List<BdModel> userList = bdModelService.getAllModelByPage(map);
		pageEntity.setList(userList);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	  
	  @RequestMapping("/removeModel")
	  @ResponseBody
	  public int removeFittingType(HttpServletRequest request,Model model){
		int id = Integer.parseInt(request.getParameter("id"));
	    return this.bdModelService.removeModel(id);
	  }

	  
	  @RequestMapping("/saveModel")
	  @ResponseBody
	  public Message saveModel(HttpServletRequest request,Model model){
		Message message = new Message();
		String str = request.getParameter("model");
		BdModel bean = JSON.parseObject(str, BdModel.class);
		BdModel result = new BdModel();
		try {
			result = this.bdModelService.saveModel(bean);
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
	  
}
