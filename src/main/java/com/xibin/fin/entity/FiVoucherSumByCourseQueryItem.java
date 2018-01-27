package com.xibin.fin.entity;

import java.security.KeyStore.PrivateKeyEntry;
import java.time.Period;
import java.util.Date;

import com.xibin.fin.pojo.FiVoucherDetail;

public class FiVoucherSumByCourseQueryItem {
	
	private String courseNo;
	
	private String courseName;
	
	private String period;
	
	private Double sumDebit;
	
	private Double sumCredit;
	
	private String toGo;
	
	private Double balance;

	public String getCourseNo() {
		return courseNo;
	}

	public void setCourseNo(String courseNo) {
		this.courseNo = courseNo;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Double getSumDebit() {
		return sumDebit;
	}

	public void setSumDebit(Double sumDebit) {
		this.sumDebit = sumDebit;
	}

	public Double getSumCredit() {
		return sumCredit;
	}

	public void setSumCredit(Double sumCredit) {
		this.sumCredit = sumCredit;
	}

	public String getToGo() {
		return toGo;
	}

	public void setToGo(String toGo) {
		this.toGo = toGo;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	
}
