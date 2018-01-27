package com.xibin.fin.entity;

import java.io.Serializable;

public class FiCourseBalanceForwardQueryItem implements Serializable{

	private Double debitBalance;
	private Double creditBalance;
	private String courseNo;
	
	public Double getDebitBalance() {
		return debitBalance;
	}
	public void setDebitBalance(Double debitBalance) {
		this.debitBalance = debitBalance;
	}
	public Double getCreditBalance() {
		return creditBalance;
	}
	public void setCreditBalance(Double creditBalance) {
		this.creditBalance = creditBalance;
	}
	public String getCourseNo() {
		return courseNo;
	}
	public void setCourseNo(String courseNo) {
		this.courseNo = courseNo;
	}
	
	
	
}
