package com.xibin.wms.controller;

import java.util.ArrayList;
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
import com.xibin.fin.service.UtVoucherService;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.query.WmInboundDetailQueryItem;
import com.xibin.wms.query.WmInboundDetailSkuQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmInboundRecieveQueryItem;
import com.xibin.wms.service.WmInboundDetailService;
import com.xibin.wms.service.WmInboundHeaderService;
import com.xibin.wms.service.WmInboundReceiveService;

@Controller
@RequestMapping(value = "/inbound", produces = { "application/json;charset=UTF-8" })
public class InboundController {
	@Resource
	private WmInboundHeaderService inboundHeaderService;
	@Resource
	private WmInboundDetailService inboundDetailService;
	@Resource
	private WmInboundReceiveService inboundReceiveService;
	@Autowired
	private UtVoucherService utVoucherService;
	@Autowired
	private HttpSession session;

	@RequestMapping("/showAllInboundOrder")
	@ResponseBody
	public PageEntity<WmInboundHeaderQueryItem> showAllInboundOrder(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmInboundHeaderQueryItem> pageEntity = new PageEntity<WmInboundHeaderQueryItem>();
		Page<?> page = new Page();
		Map map = new HashMap<>();
		// 配置分页参数
		if (request.getParameter("page") != null && request.getParameter("size") != null) {
			page.setPageNo(Integer.parseInt(request.getParameter("page")));
			page.setPageSize(Integer.parseInt(request.getParameter("size")));
			map = JSONObject.parseObject(request.getParameter("conditions"));
			map.put("page", page);

		} else {
			map = JSONObject.parseObject(request.getParameter("conditions"));
		}
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmInboundHeaderQueryItem> list = inboundHeaderService.getAllInboundOrderByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllInboundDetail")
	@ResponseBody
	public PageEntity<WmInboundDetailQueryItem> showAllInboundDetail(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmInboundDetailQueryItem> pageEntity = new PageEntity<WmInboundDetailQueryItem>();
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
		List<WmInboundDetailQueryItem> list = inboundDetailService.getAllInboundDetailByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/showAllClosedOrderInboundDetail")
	@ResponseBody
	public List<WmInboundDetailQueryItem> showAllClosedOrderInboundDetail(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Map map = JSONObject.parseObject(request.getParameter("conditions"));
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		return inboundDetailService.selectClosedOrderDetail(map);

	}

	@RequestMapping("/showAllInboundRecieve")
	@ResponseBody
	public PageEntity<WmInboundRecieveQueryItem> showAllInboundRecieve(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmInboundRecieveQueryItem> pageEntity = new PageEntity<WmInboundRecieveQueryItem>();
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
		List<WmInboundRecieveQueryItem> list = inboundReceiveService.getAllInboundRecByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	@RequestMapping("/saveInboundOrder")
	@ResponseBody
	public Message saveInboundOrder(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("order");
		WmInboundHeader bean = JSON.parseObject(str, WmInboundHeader.class);
		try {
			WmInboundHeaderQueryItem queryItem = this.inboundHeaderService.saveInbound(bean);
			message.setCode(200);
			message.setData(queryItem);
			message.setMsg("操作成功！");

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/createVoucher")
	@ResponseBody
	public Message createVoucher(@RequestParam("ids") String ids, @RequestParam("period") String period,
			@RequestParam("type") String type) {
		return utVoucherService.createVoucher(ids.split(","), period, type);
	}

	@RequestMapping("/removeInboundDetail")
	@ResponseBody
	public Message removeInboundDetail(@RequestParam("ids") int[] ids, @RequestParam("orderNo") String orderNo) {
		Message message = new Message();
		try {
			this.inboundDetailService.removeInboundDetail(ids, orderNo);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/saveInboundDetail")
	@ResponseBody
	public Message saveInboundDetail(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("detail");
		WmInboundDetail bean = JSON.parseObject(str, WmInboundDetail.class);
		try {
			this.inboundDetailService.saveInboundDetail(bean);
			message.setCode(200);
			message.setMsg("操作成功！");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/getInboundHeaderByOderNo")
	@ResponseBody
	public WmInboundHeaderQueryItem getInboundHeaderByOderNo(HttpServletRequest request, Model model) {
		String orderNo = request.getParameter("orderNo");
		List<WmInboundHeaderQueryItem> list = inboundHeaderService.selectByKey(orderNo);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return new WmInboundHeaderQueryItem();
	}

	@RequestMapping("/receiveByDetailRecIds")
	@ResponseBody
	public Message receiveByDetailRecIds(@RequestParam("detailRecIds") int[] detailRecIds) {
		Message message = new Message();

		return message;
	}

	@RequestMapping("/receive")
	@ResponseBody
	public Message receive(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("receive");
		WmInboundRecieve bean = JSON.parseObject(str, WmInboundRecieve.class);
		try {
			message = this.inboundReceiveService.receiveByRecieve(bean);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/batchReceive")
	@ResponseBody
	public Message batchReceive(@RequestParam("orderNo") String orderNo, @RequestParam("lineNos") String lineNos,
			@RequestParam("recLineNos") String recLineNos) {
		Message message = new Message();
		String[] lineNoArray = lineNos.split(",");
		String[] recLineNoArray = recLineNos.split(",");
		if (lineNoArray.length != recLineNoArray.length) {
			message.setCode(0);
			message.setMsg("数据有误，请刷新单据，尝试刷新后仍出现请联系管理员");
			return message;
		}
		List<String> errors = new ArrayList<String>();
		Message receiveReturnMsg = new Message();
		for (int i = 0; i < lineNoArray.length; i++) {
			try {
				receiveReturnMsg = this.inboundReceiveService.receiveByLineNoAndRecLineNO(orderNo, lineNoArray[i],
						recLineNoArray[i]);
				if (receiveReturnMsg.getCode() == 0) {
					errors.add(receiveReturnMsg.getMsg());
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errors.add(e.getMessage());
			}
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("全部操作成功！");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
			message.converMsgsToMsg("</br>");
		}
		return message;
	}

	@RequestMapping("/cancelReceive")
	@ResponseBody
	public Message cancelReceive(HttpServletRequest request, Model model) {
		Message message = new Message();
		String str = request.getParameter("receive");
		WmInboundRecieve bean = JSON.parseObject(str, WmInboundRecieve.class);
		try {
			message = this.inboundReceiveService.cancelReceiveByRecieve(bean);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	@RequestMapping("/audit")
	@ResponseBody
	public Message audit(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmInboundHeaderQueryItem queryItem = new WmInboundHeaderQueryItem();
		try {
			for (String orderNo : orderNos) {
				queryItem = inboundHeaderService.audit(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderNos.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	@RequestMapping("/cancelAudit")
	@ResponseBody
	public Message cancelAudit(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		WmInboundHeaderQueryItem queryItem = new WmInboundHeaderQueryItem();
		try {
			for (String orderNo : orderNos) {
				queryItem = inboundHeaderService.cancelAudit(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderNos.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	@RequestMapping("/close")
	@ResponseBody
	public Message close(@RequestParam("orderNos") String orderNos) {
		Message message = new Message();
		String[] orderArray = orderNos.split(",");
		List<String> errors = new ArrayList<String>();
		WmInboundHeaderQueryItem queryItem = new WmInboundHeaderQueryItem();
		try {
			for (String orderNo : orderArray) {
				queryItem = inboundHeaderService.close(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
			if (orderArray.length == 1) {
				message.setData(queryItem);
			}
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("</br>");
		return message;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public Message remove(@RequestParam("orderNos") String[] orderNos) {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		try {
			for (String orderNo : orderNos) {
				inboundHeaderService.remove(orderNo);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errors.add(e.getMessage());
		}
		if (errors.size() == 0) {
			message.setCode(200);
			message.setMsg("操作成功！");
		} else {
			message.setCode(100);
			message.setMsgs(errors);
		}
		message.converMsgsToMsg("");
		return message;
	}

	@RequestMapping("/accountByOrderNo")
	@ResponseBody
	public Message accountByOrderNo(HttpServletRequest request, Model model) {
		String orderNo = request.getParameter("orderNo");
		Message message = new Message();
		try {
			message = inboundHeaderService.accountByOrderNo(orderNo);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/accountCostByOrderNo")
	@ResponseBody
	public Message accountCostByOrderNo(HttpServletRequest request, Model model) {
		String orderNo = request.getParameter("orderNo");
		Message message = new Message();
		try {
			message = inboundHeaderService.accountForCostByOrderNo(orderNo);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/accountByOrderNos")
	@ResponseBody
	public Message accountByOrderNos(@RequestParam("orderNos") String orderNos,
			@RequestParam("inboundType") String inboundType) {
		List<String> orderNoList = new ArrayList<String>();
		String[] orderArray = orderNos.split(",");
		for (String orderNo : orderArray) {
			orderNoList.add(orderNo);
		}
		Message message = new Message();
		try {
			message = inboundHeaderService.accountByOrderNos(orderNoList, inboundType);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/accountCostByOrderNos")
	@ResponseBody
	public Message accountCostByOrderNos(@RequestParam("orderNos") String orderNos,
			@RequestParam("inboundType") String inboundType) {
		List<String> orderNoList = new ArrayList<String>();
		String[] orderArray = orderNos.split(",");
		for (String orderNo : orderArray) {
			orderNoList.add(orderNo);
		}
		Message message = new Message();
		try {
			message = inboundHeaderService.accountForCostByOrderNos(orderNoList, inboundType);
			return message;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}

	@RequestMapping("/queryWmInboundDetailByPage")
	@ResponseBody
	public PageEntity<WmInboundDetailSkuQueryItem> queryWmInboundDetailByPage(HttpServletRequest request, Model model) {
		// 开始分页
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		PageEntity<WmInboundDetailSkuQueryItem> pageEntity = new PageEntity<WmInboundDetailSkuQueryItem>();
		Page<?> page = new Page();
		Map map = new HashMap<>();
		// 配置分页参数
		if (request.getParameter("page") != null && request.getParameter("size") != null) {
			page.setPageNo(Integer.parseInt(request.getParameter("page")));
			page.setPageSize(Integer.parseInt(request.getParameter("size")));
			map = JSONObject.parseObject(request.getParameter("conditions"));
			map.put("page", page);

		} else {
			map = JSONObject.parseObject(request.getParameter("conditions"));
		}
		if (userDetails != null) {
			map.put("companyId", userDetails.getCompanyId());
			map.put("warehouseId", userDetails.getWarehouseId());
		}
		List<WmInboundDetailSkuQueryItem> list = inboundDetailService.queryWmInboundDetailByPage(map);
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

}
