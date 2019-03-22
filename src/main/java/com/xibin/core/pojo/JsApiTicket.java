package com.xibin.core.pojo;

public class JsApiTicket {

	private String ticket; // 票据
	private int expiresIn;// 凭证有效时间，单位：秒</span>

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
}
