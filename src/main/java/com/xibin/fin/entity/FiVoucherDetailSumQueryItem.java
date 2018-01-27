package com.xibin.fin.entity;



public class FiVoucherDetailSumQueryItem {
	
	private String  courseNo;
	
	private String type;
	
	private String toGo;
	
	private Double balance;
	
	private Double sumDebit;
	
	private Double sumCredit;

	public String getCourseNo() {
		return courseNo;
	}

	public void setCourseNo(String courseNo) {
		this.courseNo = courseNo;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getToGo() {
		return toGo;
	}

	public void setToGo(String toGo) {
		this.toGo = toGo;
	}

	
	
}
