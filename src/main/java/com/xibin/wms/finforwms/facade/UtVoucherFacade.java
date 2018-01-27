package com.xibin.wms.finforwms.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.spi.RetryableUnavailableException;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.pojo.Message;
import com.xibin.core.utils.CustomizedPropertyConfigurer;
import com.xibin.core.utils.HttpRequestUtil;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmOutboundDetail;
@Service
public class UtVoucherFacade {

	public Message createVoucher(List<WmInboundDetail> details,String period,String userName,String type){
		Message message = new Message();
		String url = (String) CustomizedPropertyConfigurer.getContextProperty("finAddress");
		url += "utvoucher/createVoucher.shtml";
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("details", JSON.toJSONString(details));
		paramMap.put("period", period);
		paramMap.put("userName", userName);
		paramMap.put("type", type);
		JSONObject result = HttpRequestUtil.httpPost(url, paramMap, false);
		if(result!=null){
			message = JSONObject.toJavaObject(result, Message.class);
			return message;
		}else{
			return null;
		}
	}
	public Message createVoucherByOutbound(List<WmOutboundDetail> details,String period,String userName,String type){
		Message message = new Message();
		String url = (String) CustomizedPropertyConfigurer.getContextProperty("finAddress");
		url += "utvoucher/createVoucherByOutbound.shtml";
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("details", JSON.toJSONString(details));
		paramMap.put("period", period);
		paramMap.put("userName", userName);
		paramMap.put("type", type);
		JSONObject result = HttpRequestUtil.httpPost(url, paramMap, false);
		if(result!=null){
			message = JSONObject.toJavaObject(result, Message.class);
			return message;
		}else{
			return null;
		}
	}
	
	
	
	
	
}
