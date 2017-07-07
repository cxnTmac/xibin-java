package com.xibin.core.pojo;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3288281729089080931L;
	private String msg;
	private List<String> msgs;
	private int code;
	private Object data;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public List<String> getMsgs() {
		return msgs;
	}
	public void setMsgs(List<String> msgs) {
		this.msgs = msgs;
	}
	
	public void converMsgsToMsg(String splitStr){
		StringBuffer stringBuffer = new StringBuffer();
		List<String> msgList = this.msgs;
		if(msgList!=null){
			for(String msg : msgList){
				stringBuffer.append(msg);
				stringBuffer.append(splitStr);
			}
			this.msg = stringBuffer.toString();
		}
	}
}
