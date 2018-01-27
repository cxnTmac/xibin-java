package com.xibin.fin.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class FiCourseBalance extends BaseModel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 33040803802413473L;

	private Integer id;

    private String courseNo;
    
    private String period;
    
    private Double startBalance;
    
    private Double endBalance;
    
    private Double sumDebit;
    
    private Double sumCredit;
    
    private Double accumulateDebit;
    
    private Double accumulateCredit;
    
    private Integer bookId;

    private String remark;

    private Integer creator;

    private Date createTime;

    private Integer modifier;

    private Date modifyTime;

    private Integer recVer;

    private Integer companyId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCourseNo() {
		return courseNo;
	}

	public void setCourseNo(String courseNo) {
		this.courseNo = courseNo;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
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

	public Double getAccumulateDebit() {
		return accumulateDebit;
	}

	public void setAccumulateDebit(Double accumulateDebit) {
		this.accumulateDebit = accumulateDebit;
	}

	public Double getAccumulateCredit() {
		return accumulateCredit;
	}

	public void setAccumulateCredit(Double accumulateCredit) {
		this.accumulateCredit = accumulateCredit;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Integer getRecVer() {
		return recVer;
	}

	public void setRecVer(Integer recVer) {
		this.recVer = recVer;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	
    
}