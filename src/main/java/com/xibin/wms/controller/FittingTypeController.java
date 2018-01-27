package com.xibin.wms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.swing.internal.plaf.metal.resources.metal_zh_TW;
import com.xibin.core.costants.Constants;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.service.BdFittingTypeService;

@Controller
@RequestMapping(value = "/fittingType", produces = {"application/json;charset=UTF-8"})
public class FittingTypeController {
	@Resource
	private BdFittingTypeService fittingTypeService;
	@Autowired  
	private HttpSession session;
	@RequestMapping("/showAllFittingType")
	@ResponseBody
	public PageEntity<BdFittingType> showAllFittingType(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdFittingType> pageEntity = new PageEntity<BdFittingType>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BdFittingType> userList = fittingTypeService.getAllFittingTypeByPage(map);
		pageEntity.setList(userList);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	 }
	@RequestMapping("/MshowAllFittingTypeWithOutPage")
	@ResponseBody
	public List<JSONObject> showAllFittingTypeWithOutPage(HttpServletRequest request,Model model){
		Map map = new HashMap<>();
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		if(userDetails != null){
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BdFittingType> typeList = fittingTypeService.getAllFittingTypeByPage(map);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for(BdFittingType type:typeList){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("fittingTypeCode", type.getFittingTypeCode());
			jsonObject.put("fittingTypeName", type.getFittingTypeName());
			jsonList.add(jsonObject);
		}
		return jsonList;
	}
	  @RequestMapping("/removeFittingType")
	  @ResponseBody
	  public Message removeFittingType(HttpServletRequest request,Model model){
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String fittingTypeCode = request.getParameter("fittingTypeCode");
	    try {
			this.fittingTypeService.removeFittingType(id,fittingTypeCode);
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
	  public Message batchRemove(@RequestParam("ids") int [] ids,@RequestParam("fittingTypeCodes") String [] fittingTypeCodes){
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0;i<ids.length;i++){
			try {
				this.fittingTypeService.removeFittingType(ids[i],fittingTypeCodes[i]);
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
	  
	  @RequestMapping("/saveFittingType")
	  @ResponseBody
	  public Message saveFittingType(HttpServletRequest request,Model model){
		Message message = new Message();
		String str = request.getParameter("fittingType");
		BdFittingType bean = JSON.parseObject(str, BdFittingType.class);
		BdFittingType result = new BdFittingType();
		try {
			result = this.fittingTypeService.saveFittingType(bean);
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
