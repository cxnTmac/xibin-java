package com.xibin.core.page.pojo;

import java.io.Serializable;
import java.util.List;

public class PageEntity<T> implements Serializable{
	private Long size;
	private  List<T> list;
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
}
