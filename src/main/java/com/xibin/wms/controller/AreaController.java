package com.xibin.wms.controller;

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
import com.xibin.wms.pojo.BdArea;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.service.BdAreaService;
import com.xibin.wms.service.BdFittingSkuPicService;
import com.xibin.wms.service.BdFittingSkuService;
import com.xibin.wms.service.BdFittingTypeService;

@Controller
@RequestMapping(value = "/area", produces = {"application/json;charset=UTF-8"})
public class AreaController {
	@Resource
	private BdAreaService bdAreaService;
	@Autowired  
	private HttpSession session;
	
	@RequestMapping("/showAllArea")
	@ResponseBody
	public PageEntity<BdArea> showAllArea(HttpServletRequest request,Model model){ 
	    // 开始分页  
		UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdArea> pageEntity = new PageEntity<BdArea>();
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
		List<BdArea> list = bdAreaService.getAllAreaByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
	    return  pageEntity;
	}
	
	
	@RequestMapping("/getAreaByAreaCode")
	@ResponseBody
	public BdArea getAreaByAreaCode(HttpServletRequest request,Model model){
		String areaCode = request.getParameter("areaCode");
		List<BdArea> list = bdAreaService.selectByKey(areaCode);
		if(!list.isEmpty()){
			return list.get(0);
		}
		return new BdArea();
	}
	
	
	@RequestMapping("/removeArea")
	@ResponseBody
	public Message removeArea(HttpServletRequest request,Model model){
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String areaCode = request.getParameter("areaCode");
	    try {
			this.bdAreaService.removeArea(id,areaCode);
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
	  public Message batchRemove(@RequestParam("ids") int [] ids,@RequestParam("areaCodes") String [] areaCodes){
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0;i<ids.length;i++){
			try {
				this.bdAreaService.removeArea(ids[i],areaCodes[i]);
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
	  
	  @RequestMapping("/saveArea")
	  @ResponseBody
	  public Message saveArea(HttpServletRequest request,Model model){
		Message message = new Message();
		String str = request.getParameter("area");
		BdArea bean = JSON.parseObject(str, BdArea.class);
		BdArea result = new BdArea();
		try {
			result = this.bdAreaService.saveArea(bean);
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
