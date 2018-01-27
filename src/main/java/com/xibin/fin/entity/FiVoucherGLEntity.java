package com.xibin.fin.entity;

import java.security.KeyStore.PrivateKeyEntry;
import java.time.Period;
import java.util.Date;

import com.xibin.fin.pojo.FiVoucherDetail;

public class FiVoucherGLEntity extends FiVoucherSumByCourseQueryItem{
	
	private String summary;
	
	private Double balance;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	
	
	
}
