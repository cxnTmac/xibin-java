package com.xibin.wms.controller;

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
import com.xibin.wms.pojo.BdFittingSkuGroup;
import com.xibin.wms.query.BdFittingSkuGroupQueryItem;
import com.xibin.wms.service.BdFittingSkuGroupService;

@Controller
@RequestMapping(value = "/skuGroup", produces = { "application/json;charset=UTF-8" })
public class SkuGroupController {
	@Resource
	private BdFittingSkuGroupService bdFittingSkuGroupService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllSkuGroup")
	@ResponseBody
	public PageEntity<BdFittingSkuGroupQueryItem> showAllSkuGroup(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdFittingSkuGroupQueryItem> pageEntity = new PageEntity<BdFittingSkuGroupQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<BdFittingSkuGroupQueryItem> list = bdFittingSkuGroupService.getAllSkuGropuByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/getSkuGroupByGroupCode")
	@ResponseBody
	public BdFittingSkuGroup getSkuGroupByGroupCode(HttpServletRequest request, Model model) {
		String groupCode = request.getParameter("groupCode");
		List<BdFittingSkuGroup> list = bdFittingSkuGroupService.selectByKey(groupCode);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new BdFittingSkuGroup();
	}

	@RequestMapping("/getSkuGroupQueryItemByGroupCode")
	@ResponseBody
	public List<BdFittingSkuGroupQueryItem> getSkuGroupQueryItemByGroupCode(HttpServletRequest request, Model model) {
		String groupCode = request.getParameter("groupCode");
		List<BdFittingSkuGroupQueryItem> list = bdFittingSkuGroupService.queryItemByKey(groupCode);
		return list;
	}

	@RequestMapping("/removeSkuGroup")
	@ResponseBody
	public Message removeSkuGroup(HttpServletRequest request, Model model) {
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String groupCode = request.getParameter("groupCode");
		try {
			this.bdFittingSkuGroupService.removeSkuGroup(id, groupCode);
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
	public Message batchRemove(@RequestParam("ids") int[] ids, @RequestParam("groupCodes") String[] groupCodes) {
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			try {
				this.bdFittingSkuGroupService.removeSkuGroup(ids[i], groupCodes[i]);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stringBuffer.append(e.getMessage() + "</br>");
			}
		}
		if (stringBuffer.length() > 0) {
			message.setMsg(stringBuffer.toString());
			message.setCode(0);
			return message;
		}
		message.setCode(200);
		message.setMsg("全部删除成功");
		return message;
	}

	@RequestMapping("/saveSkuGroup")
	@ResponseBody
	public Message saveSkuGroup(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("skuGroup");
		BdFittingSkuGroup bean = JSON.parseObject(str, BdFittingSkuGroup.class);
		BdFittingSkuGroup result = new BdFittingSkuGroup();
		try {
			result = this.bdFittingSkuGroupService.saveSkuGroup(bean);
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
