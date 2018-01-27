package com.xibin.fin.entity;

import java.security.KeyStore.PrivateKeyEntry;
import java.time.Period;
import java.util.Date;

import com.xibin.fin.pojo.FiCourseBalance;
import com.xibin.fin.pojo.FiVoucherDetail;

public class FiCourseBalanceGLQueryItem extends FiCourseBalance{
	
	
	private String courseName;
	
	private String toGo;

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getToGo() {
		return toGo;
	}

	public void setToGo(String toGo) {
		this.toGo = toGo;
	}
	

	
	
	
}
