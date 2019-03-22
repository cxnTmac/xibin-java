package com.xibin.fin.entity;

import java.io.Serializable;

public class FiCourseBalanceBSQueryItem implements Serializable {
	private String courseNo;
	private String type;
	private String toGo;
	private String isChild;
	private Double startBalance;
	private Double endBalance;
	private Double sumCredit;
	private Double sumDebit;
	private Double accumulateCredit;
	private Double accumulateDebit;
	private Double yearBalance;

	public String getCourseNo() {
		return courseNo;
	}

	public void setCourseNo(String courseNo) {
		this.courseNo = courseNo;
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

	public String getIsChild() {
		return isChild;
	}

	public void setIsChild(String isChild) {
		this.isChild = isChild;
	}

	public Double getStartBalance() {
		return startBalance;
	}

	public void setStartBalance(Double startBalance) {
		this.startBalance = startBalance;
	}

	public Double getEndBalance() {
		return endBalance;
	}

	public void setEndBalance(Double endBalance) {
		this.endBalance = endBalance;
	}

	public Double getSumCredit() {
		return sumCredit;
	}

	public void setSumCredit(Double sumCredit) {
		this.sumCredit = sumCredit;
	}

	public Double getSumDebit() {
		return sumDebit;
	}

	public void setSumDebit(Double sumDebit) {
		this.sumDebit = sumDebit;
	}

	public Double getAccumulateCredit() {
		return accumulateCredit;
	}

	public void setAccumulateCredit(Double accumulateCredit) {
		this.accumulateCredit = accumulateCredit;
	}

	public Double getAccumulateDebit() {
		return accumulateDebit;
	}

	public void setAccumulateDebit(Double accumulateDebit) {
		this.accumulateDebit = accumulateDebit;
	}

	public Double getYearBalance() {
		return yearBalance;
	}

	public void setYearBalance(Double yearBalance) {
		this.yearBalance = yearBalance;
	}

}
