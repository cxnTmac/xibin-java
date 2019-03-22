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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.costants.Constants;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.pojo.BdFittingSkuAssemble;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.query.BdFittingSkuAssembleQueryItem;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.service.BdFittingSkuAssembleService;
import com.xibin.wms.service.BdFittingSkuPicService;
import com.xibin.wms.service.BdFittingSkuService;

@Controller
@RequestMapping(value = "/fittingSku", produces = { "application/json;charset=UTF-8" })
public class FittingSkuController {
	@Resource
	private BdFittingSkuService fittingSkuService;
	@Resource
	private BdFittingSkuPicService fittingSkuPicService;
	@Resource
	private BdFittingSkuAssembleService fittingSkuAssembleService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllFittingSku")
	@ResponseBody
	public PageEntity<BdFittingSkuQueryItem> showAllFittingSku(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdFittingSkuQueryItem> pageEntity = new PageEntity<BdFittingSkuQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("orderCondition", "ORDER BY bfs.fitting_sku_code");
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BdFittingSkuQueryItem> list = fittingSkuService.getAllFittingSkuByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/getFittingTypeSkuBySkuCode")
	@ResponseBody
	public BdFittingSkuQueryItem getFittingTypeSkuBySkuCode(HttpServletRequest request, Model model) {
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		List<BdFittingSkuQueryItem> list = fittingSkuService.selectByKey(fittingSkuCode);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new BdFittingSkuQueryItem();
	}

	@RequestMapping("/MshowAllFittingSku")
	@ResponseBody
	public PageEntity<BdFittingSkuQueryItem> MshowAllFittingSku(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdFittingSkuQueryItem> pageEntity = new PageEntity<BdFittingSkuQueryItem>();
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BdFittingSkuQueryItem> list = fittingSkuService.MgetAllFittingSkuByPageWithOnePic(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/getFittingSkuPic")
	@ResponseBody
	public List<BdFittingSkuPic> getFittingSkuPic(HttpServletRequest request, Model model) {
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		return fittingSkuPicService.selectByFittingSkuCode(fittingSkuCode);
	}

	@RequestMapping("/removeFittingSku")
	@ResponseBody
	public Message removeFittingSku(HttpServletRequest request, Model model) {
		Message message = new Message();
		int id = Integer.parseInt(request.getParameter("id"));
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		try {
			this.fittingSkuService.removeFittingSku(id, fittingSkuCode);
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
	public Message batchRemove(@RequestParam("ids") int[] ids,
			@RequestParam("fittingSkuCodes") String[] fittingSkuCodes) {
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			try {
				this.fittingSkuService.removeFittingSku(ids[i], fittingSkuCodes[i]);
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

	@RequestMapping("/saveFittingSku")
	@ResponseBody
	public Message saveFittingSku(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("fittingSku");
		BdFittingSku bean = JSON.parseObject(str, BdFittingSku.class);
		BdFittingSku result = new BdFittingSku();
		try {
			result = this.fittingSkuService.saveFittingSku(bean);
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

	@RequestMapping("/removeFittingSkuPic")
	@ResponseBody
	public Message removeFittingSkuPic(@RequestParam("idNormal") int idNoromal, @RequestParam("idZip") int idZip,
			@RequestParam("fittingSkuCode") String fittingSkuCode) {
		Message message = new Message();
		this.fittingSkuPicService.removeFittingSkuPic(idNoromal, idZip);
		message.setCode(200);
		message.setMsg("删除成功");
		List<BdFittingSkuPic> results = fittingSkuPicService.selectByFittingSkuCode(fittingSkuCode);
		message.setData(results);
		return message;
	}

	@RequestMapping("/saveSkuAssemble")
	@ResponseBody
	public Message saveSkuAssemble(HttpServletRequest request, Model model) {
		Message message = new Message();
		List<BdFittingSkuAssemble> details = JSONObject.parseArray(request.getParameter("details"),
				BdFittingSkuAssemble.class);
		List<BdFittingSkuAssemble> removeDetails = JSONObject.parseArray(request.getParameter("removeDetails"),
				BdFittingSkuAssemble.class);
		return fittingSkuAssembleService.saveFittingSkuAssemble(details, removeDetails);
	}

	@RequestMapping("/querySkuAssembleByFSkuCode")
	@ResponseBody
	public PageEntity<BdFittingSkuAssembleQueryItem> saveVoucherAndDetail(HttpServletRequest request, Model model) {
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<BdFittingSkuAssembleQueryItem> pageEntity = new PageEntity<BdFittingSkuAssembleQueryItem>();
		Page<?> page = new Page();
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		map.put("page", page);
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
		}
		List<BdFittingSkuAssembleQueryItem> list = fittingSkuAssembleService.getAllFittingSkuByFSkuCode(map);
		pageEntity.setList(list);
		pageEntity.setSize((long) list.size());
		return pageEntity;
	}

	@RequestMapping(value = "/importSkuCodesByExcel", consumes = "multipart/form-data", method = RequestMethod.POST)
	@ResponseBody
	public Message importSkuCodesByExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		String skuCodeColumnName = request.getParameter("skuCodeColumnName");
		try {
			return fittingSkuService.importByExcel(file, skuCodeColumnName);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message message = new Message();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}
}
