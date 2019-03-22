package com.xibin.fin.entity;

import java.util.Date;

import com.xibin.fin.pojo.FiVoucherDetail;

public class FiVoucherEntity extends FiVoucherDetail {

	private Integer voucherNum;

	private String voucherWord;

	private String period;

	private Date billDate;

	private String status;

	private Double bill;

	private String checkerName;

	private Date completeDate;

	private String courseName;

	private String auxiliaryName;

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public Integer getVoucherNum() {
		return voucherNum;
	}

	public void setVoucherNum(Integer voucherNum) {
		this.voucherNum = voucherNum;
	}

	public String getVoucherWord() {
		return voucherWord;
	}

	public void setVoucherWord(String voucherWord) {
		this.voucherWord = voucherWord;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Date getBillDate() {
		return billDate;
	}

	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getBill() {
		return bill;
	}

	public void setBill(Double bill) {
		this.bill = bill;
	}

	public String getCheckerName() {
		return checkerName;
	}

	public void setCheckerName(String checkerName) {
		this.checkerName = checkerName;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public String getAuxiliaryName() {
		return auxiliaryName;
	}

	public void setAuxiliaryName(String auxiliaryName) {
		this.auxiliaryName = auxiliaryName;
	}

}
