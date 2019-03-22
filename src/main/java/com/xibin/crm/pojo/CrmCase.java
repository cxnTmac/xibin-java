package com.xibin.crm.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class CrmCase extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 33040803802413473L;

	private Integer id;

	private String nameCn;

	private String nameEn;

	private String oldStuNum;

	private String stuNum;

	private String status;

	private Date caseTime;

	private String visa;

	private String country;

	private String inChargeOfCase;

	private String inChargeOfVisa;

	private Date enrolmentTime;

	private Double tutition;

	private Double commissionRate;

	private String commissionType;

	private Integer creator;

	private Date createTime;

	private Integer modifier;

	private Date modifyTime;

	private Integer recVer;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getNameCn() {
		return nameCn;
	}

	public void setNameCn(String nameCn) {
		this.nameCn = nameCn;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getOldStuNum() {
		return oldStuNum;
	}

	public void setOldStuNum(String oldStuNum) {
		this.oldStuNum = oldStuNum;
	}

	public String getStuNum() {
		return stuNum;
	}

	public void setStuNum(String stuNum) {
		this.stuNum = stuNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCaseTime() {
		return caseTime;
	}

	public void setCaseTime(Date caseTime) {
		this.caseTime = caseTime;
	}

	public String getVisa() {
		return visa;
	}

	public void setVisa(String visa) {
		this.visa = visa;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getInChargeOfCase() {
		return inChargeOfCase;
	}

	public void setInChargeOfCase(String inChargeOfCase) {
		this.inChargeOfCase = inChargeOfCase;
	}

	public String getInChargeOfVisa() {
		return inChargeOfVisa;
	}

	public void setInChargeOfVisa(String inChargeOfVisa) {
		this.inChargeOfVisa = inChargeOfVisa;
	}

	public Date getEnrolmentTime() {
		return enrolmentTime;
	}

	public void setEnrolmentTime(Date enrolmentTime) {
		this.enrolmentTime = enrolmentTime;
	}

	public Double getTutition() {
		return tutition;
	}

	public void setTutition(Double tutition) {
		this.tutition = tutition;
	}

	public Double getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(Double commissionRate) {
		this.commissionRate = commissionRate;
	}

	public String getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
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

}