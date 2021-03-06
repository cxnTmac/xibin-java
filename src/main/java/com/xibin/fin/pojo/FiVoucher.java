package com.xibin.fin.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class FiVoucher extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 33040803802413473L;

	private Integer id;

	private Integer voucherNum;

	private String voucherWord;

	private String period;

	private Date billDate;

	private String status;

	private String fromOrderType;

	private Double bill;

	private Integer checker;

	private Date completeDate;

	private String remark;

	private Integer creator;

	private Date createTime;

	private Integer modifier;

	private Date modifyTime;

	private Integer recVer;

	private Integer companyId;

	private Integer bookId;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
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

	public Integer getChecker() {
		return checker;
	}

	public void setChecker(Integer checker) {
		this.checker = checker;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public Integer getCreator() {
		return creator;
	}

	@Override
	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public Integer getModifier() {
		return modifier;
	}

	@Override
	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	@Override
	public Date getModifyTime() {
		return modifyTime;
	}

	@Override
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Override
	public Integer getRecVer() {
		return recVer;
	}

	@Override
	public void setRecVer(Integer recVer) {
		this.recVer = recVer;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String wmToFinService() {
		return fromOrderType;
	}

	public void setFromOrderType(String fromOrderType) {
		this.fromOrderType = fromOrderType;
	}

	public String getFromOrderType() {
		return fromOrderType;
	}

}