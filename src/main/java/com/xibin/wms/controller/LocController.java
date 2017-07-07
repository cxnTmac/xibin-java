package com.xibin.wms.controller;

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
import com.xibin.wms.pojo.BdLoc;
import com.xibin.wms.pojo.BdZone;
import com.xibin.wms.query.BdLocQueryItem;
import com.xibin.wms.query.BdZoneQueryItem;
import com.xibin.wms.service.BdLocService;
import com.xibin.wms.service.BdZoneService;

@Controller
@RequestMapping(value = "/loc", produces = {"application/json;charset=UTF-8"})
public class LocController {
	@Resource
	private BdLocService bdLocService;
	
	@RequestMapping("/showAllLoc")
	@ResponseBody
	public PageEntity<BdLocQueryItem> showAllLoc(HttpServletRequest request,Model model){ 
	    // 开始分页  
		PageEntity<BdLocQueryItem> pageEntity = new PageEntity<BdLocQueryItem>();
		Page<?> page = new Page();
		//配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		List<BdLocQueryItem> list = bdLocService.getAllLocByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	}
	
	
	@RequestMapping("/getLocByLocCode")
	@ResponseBody
	public BdLocQueryItem getLocByLocCode(HttpServletRequest request,Model model){
		String areaCode = request.getParameter("areaCode");
		List<BdLocQueryItem> list = bdLocService.selectByKey(areaCode);
		if(!list.isEmpty()){
			return list.get(0);
		}
		return new BdLocQueryItem();
	}
	
	
	@RequestMapping("/removeLoc")
	@ResponseBody
	public Message removeLoc(HttpServletRequest request,Model model){
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String locCode = request.getParameter("locCode");
	    try {
			this.bdLocService.removeLoc(id,locCode);
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
	  public Message batchRemove(@RequestParam("ids") int [] ids,@RequestParam("locCodes") String [] locCodes){
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0;i<ids.length;i++){
			try {
				this.bdLocService.removeLoc(ids[i],locCodes[i]);
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
	  
	  @RequestMapping("/saveLoc")
	  @ResponseBody
	  public Message saveLoc(HttpServletRequest request,Model model){
		Message message = new Message();
		String str = request.getParameter("loc");
		BdLoc bean = JSON.parseObject(str, BdLoc.class);
		BdLoc result = new BdLoc();
		try {
			result = this.bdLocService.saveLoc(bean);
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
