package com.xibin.wms.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmOutboundDetail extends BaseModel{
    private Integer id;

    private String orderNo;

    private String lineNo;
    
    private String buyerCode;

    private String skuCode;
    
    private String status;

    private Double outboundNum;
    
    private Double outboundAllocNum;
    
    private Double outboundPickNum;
    
    private Double outboundShipNum;

    private Double outboundPrice;
    
    private String planShipLoc;

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

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public Double getOutboundNum() {
		return outboundNum;
	}

	public void setOutboundNum(Double outboundNum) {
		this.outboundNum = outboundNum;
	}

	public Double getOutboundPrice() {
		return outboundPrice;
	}

	public void setOutboundPrice(Double outboundPrice) {
		this.outboundPrice = outboundPrice;
	}

	public String getPlanShipLoc() {
		return planShipLoc;
	}

	public void setPlanShipLoc(String planShipLoc) {
		this.planShipLoc = planShipLoc;
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

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getOutboundAllocNum() {
		return outboundAllocNum;
	}

	public void setOutboundAllocNum(Double outboundAllocNum) {
		this.outboundAllocNum = outboundAllocNum;
	}

	public Double getOutboundPickNum() {
		return outboundPickNum;
	}

	public void setOutboundPickNum(Double outboundPickNum) {
		this.outboundPickNum = outboundPickNum;
	}

	public Double getOutboundShipNum() {
		return outboundShipNum;
	}

	public void setOutboundShipNum(Double outboundShipNum) {
		this.outboundShipNum = outboundShipNum;
	}
	
	
}