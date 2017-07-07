package com.xibin.wms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.pojo.WmInboundDetail;
import com.xibin.wms.pojo.WmInboundHeader;
import com.xibin.wms.pojo.WmInboundRecieve;
import com.xibin.wms.query.WmInboundDetailQueryItem;
import com.xibin.wms.query.WmInboundHeaderQueryItem;
import com.xibin.wms.query.WmInboundRecieveQueryItem;

public interface WmInboundReceiveService {
	public List<WmInboundRecieve>  getInboundRecById(int []ids);
	
	public List<WmInboundRecieveQueryItem> getAllInboundRecByPage(Map map);
	
	public List<WmInboundRecieveQueryItem> selectByKey(String orderNo,String lineNo);
	
	public List<WmInboundRecieve> selectByExample(WmInboundRecieve model);
	
	public WmInboundRecieve saveInboundRec (WmInboundRecieve model);
	
	public Message receiveByRecieve(WmInboundRecieve inboundRecieve) throws BusinessException;

	public Message cancelReceiveByRecieve(WmInboundRecieve wmInboundRecieve);
	
	public Message receiveByRecieveIds(@RequestParam("detailRecIds") int [] detailRecIds);
}
