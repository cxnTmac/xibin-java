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
import com.xibin.wms.service.BdFittingTypeService;
import com.xibin.wms.service.BdModelService;

@Controller
@RequestMapping(value = "/model", produces = {"application/json;charset=UTF-8"})
public class ModelController {
	@Resource
	private BdModelService bdModelService;
	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/showAllModel")
	@ResponseBody
	public PageEntity<BdModel> showAllUser(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdModel> pageEntity = new PageEntity<BdModel>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BdModel> userList = bdModelService.getAllModelByPage(map);
		pageEntity.setList(userList);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	@RequestMapping("/MshowAllModel")
	@ResponseBody
	public List<BdModel> MshowAllModel(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BdModel> userList = bdModelService.getAllModelByPage(map);
	    return  userList;
	 }
	  @RequestMapping("/removeModel")
	  @ResponseBody
	  public Message removeFittingType(HttpServletRequest request,Model model){
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String modelCode = request.getParameter("modelCode");
		try {
			this.bdModelService.removeModel(id,modelCode);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setMsg(e.getMessage());
			message.setCode(0);
			return message;
		}
		message.setCode(200);
		message.setMsg("删除成功");
		return message;
	  }
	  
	  @RequestMapping("/batchRemove")
	  @ResponseBody
	  public Message batchRemove(@RequestParam("ids") int [] ids,@RequestParam("modelCodes") String [] modelCodes){
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0;i<ids.length;i++){
			try {
				this.bdModelService.removeModel(ids[i],modelCodes[i]);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stringBuffer.append(e.getMessage()+"</br>");
			}
		}
		if(stringBuffer.length()>0){
			message.setMsg(stringBuffer.toString());
			message.setCode(0);
			return message;
		}
		message.setCode(200);
		message.setMsg("全部删除成功");
	    return message;
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
