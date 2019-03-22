package com.xibin.wms.controller;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xibin.core.pojo.JsApiTicket;
import com.xibin.core.pojo.Message;
import com.xibin.core.utils.ServletContextUtil;
import com.xibin.core.utils.SignUtil;

@Controller
@RequestMapping(value = "/wxApi", produces = { "application/json;charset=UTF-8" })
public class WxApiController {

	private static String appId = "wxce961a786a558fb8";

	@RequestMapping("/getAccessToken")
	@ResponseBody
	public Message getAccessToken(HttpServletRequest request, Model model) {
		Message msg = new Message();
		JsApiTicket jsApiTicket = (JsApiTicket) ServletContextUtil.get().getAttribute("JSAPI_TICKET");
		Map<String, String> result = SignUtil.sign(jsApiTicket.getTicket(),
				URLDecoder.decode(request.getParameter("url")));
		result.put("appId", appId);
		msg.setCode(200);

		msg.setData(result);
		return msg;
	}

}
