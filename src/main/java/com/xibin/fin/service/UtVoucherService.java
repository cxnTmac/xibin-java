package com.xibin.fin.service;

import com.xibin.core.pojo.Message;

public interface UtVoucherService {
	public Message createVoucher(String []ids,String period,String type);
	public Message createVoucherByOutbound(String[] ids,String period,String type);
}
