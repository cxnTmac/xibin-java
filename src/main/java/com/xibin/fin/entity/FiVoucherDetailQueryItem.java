package com.xibin.fin.entity;

import java.security.KeyStore.PrivateKeyEntry;
import java.time.Period;
import java.util.Date;

import com.xibin.fin.pojo.FiVoucherDetail;

public class FiVoucherDetailQueryItem extends FiVoucherDetail{
	
	private String  courseName;
	
	private String auxiliaryName;

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getAuxiliaryName() {
		return auxiliaryName;
	}

	public void setAuxiliaryName(String auxiliaryName) {
		this.auxiliaryName = auxiliaryName;
	}
	
	
}
