package com.xibin.wms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.pojo.BdFittingType;
import com.xibin.wms.pojo.BdModel;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.service.BdFittingSkuPicService;
import com.xibin.wms.service.BdFittingSkuService;
import com.xibin.wms.service.BdFittingTypeService;
import com.xibin.wms.service.BdModelService;

@Controller
@RequestMapping(value = "/wx", produces = { "application/json;charset=UTF-8" })
public class WxProgramController {
	@Resource
	private BdFittingSkuService fittingSkuService;
	@Resource
	private BdFittingSkuPicService fittingSkuPicService;
	@Resource
	private BdFittingTypeService fittingTypeService;
	@Resource
	private BdModelService bdModelService;

	@RequestMapping("/showAllFittingSku")
	@ResponseBody
	public PageEntity<BdFittingSkuQueryItem> showAllFittingSku(HttpServletRequest request, Model model) {
		// 开始分页
		PageEntity<BdFittingSkuQueryItem> pageEntity = new PageEntity<BdFittingSkuQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		map.put("companyId", "1");
		List<BdFittingSkuQueryItem> list = fittingSkuService.MgetAllFittingSkuByPageWithOnePic(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllFittingTypeWithOutPage")
	@ResponseBody
	public List<JSONObject> showAllFittingTypeWithOutPage(HttpServletRequest request, Model model) {
		Map map = new HashMap<>();
		map.put("companyId", "1");
		List<BdFittingType> typeList = fittingTypeService.getAllFittingTypeByPage(map);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (BdFittingType type : typeList) {
			JSONObject jsonObject = new JSONObject();
			// 补丁代码，去除掉类别 “配件”
			if (type.getFittingTypeCode().equals("ZZ")) {
				continue;
			}
			jsonObject.put("fittingTypeCode", type.getFittingTypeCode());
			jsonObject.put("fittingTypeName", type.getFittingTypeName());
			jsonList.add(jsonObject);
		}
		return jsonList;
	}

	@RequestMapping("/getFittingTypeSkuBySkuCode")
	@ResponseBody
	public BdFittingSkuQueryItem getFittingTypeSkuBySkuCode(HttpServletRequest request, Model model) {
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		List<BdFittingSkuQueryItem> list = fittingSkuService.selectByKey(fittingSkuCode, "1");
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new BdFittingSkuQueryItem();
	}

	@RequestMapping("/getFittingSkuPic")
	@ResponseBody
	public List<BdFittingSkuPic> getFittingSkuPic(HttpServletRequest request, Model model) {
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		return fittingSkuPicService.selectByFittingSkuCode(fittingSkuCode, "1");
	}

	@RequestMapping("/showAllModel")
	@ResponseBody
	public List<JSONObject> MshowAllModel(HttpServletRequest request, Model model) {
		// 开始分页
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("companyId", "1");
		List<BdModel> modelList = bdModelService.getAllModelByPage(map);
		// 对车型数据做处理
		HashSet<String> set = new HashSet<String>();
		for (BdModel bdModel : modelList) {
			String modelCode = bdModel.getModelCode();

			String codes[] = modelCode.replaceAll("\\\\", "/").split("/");
			for (String code : codes) {
				set.add(code);
			}
		}
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (String code : set) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("modelCode", code);
			jsonObject.put("modelName", code);
			jsonList.add(jsonObject);
		}
		return jsonList;
	}

}
