package com.xibin.wms.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmInboundRecieve extends BaseModel{
    private Integer id;

    private String orderNo;

    private String lineNo;
    
    private String status;
    
    private String recLineNo;
    
    private String supplierCode;

    private String skuCode;
    
    private Double inboundPreNum;

    private Double inboundNum;

    private Double inboundPrice;
    
    private String planLoc;

    private String inboundLocCode;
    
    private Date recTime;

    private String remark;

    private Integer creator;

    private Date createTime;

    private Integer modifier;

    private Date modifyTime;

    private Integer recVer;

    private Integer companyId;

    private Integer warehouseId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	public String getRecLineNo() {
		return recLineNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setRecLineNo(String recLineNo) {
		this.recLineNo = recLineNo;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public Double getInboundPreNum() {
		return inboundPreNum;
	}

	public void setInboundPreNum(Double inboundPreNum) {
		this.inboundPreNum = inboundPreNum;
	}

	public Double getInboundNum() {
		return inboundNum;
	}

	public void setInboundNum(Double inboundNum) {
		this.inboundNum = inboundNum;
	}

	public Double getInboundPrice() {
		return inboundPrice;
	}

	public void setInboundPrice(Double inboundPrice) {
		this.inboundPrice = inboundPrice;
	}

	public String getPlanLoc() {
		return planLoc;
	}

	public void setPlanLoc(String planLoc) {
		this.planLoc = planLoc;
	}

	public String getInboundLocCode() {
		return inboundLocCode;
	}

	public void setInboundLocCode(String inboundLocCode) {
		this.inboundLocCode = inboundLocCode;
	}

	public Date getRecTime() {
		return recTime;
	}

	public void setRecTime(Date recTime) {
		this.recTime = recTime;
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

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

    
}