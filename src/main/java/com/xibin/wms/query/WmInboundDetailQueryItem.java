package com.xibin.wms.query;

import java.io.Serializable;
import java.util.Date;

public class WmInboundDetailQueryItem implements Serializable {
	private Integer id;

	private String orderNo;

	private String lineNo;

	private String status;

	private String headerStatus;

	private String isCalculated;

	private String supplierCode;

	private String supplierName;

	private String skuCode;

	private String quickCode;

	private String fittingSkuName;

	private String modelCode;

	private Double inboundPreNum;

	private Double inboundNum;

	private Double inboundPrice;

	private Double cost;

	private String planLoc;

	private String isCreatedVoucher;

	private String voucherNo;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
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

	public String getIsCreatedVoucher() {
		return isCreatedVoucher;
	}

	public void setIsCreatedVoucher(String isCreatedVoucher) {
		this.isCreatedVoucher = isCreatedVoucher;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
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

	public String getHeaderStatus() {
		return headerStatus;
	}

	public void setHeaderStatus(String headerStatus) {
		this.headerStatus = headerStatus;
	}

	public String getIsCalculated() {
		return isCalculated;
	}

	public void setIsCalculated(String isCalculated) {
		this.isCalculated = isCalculated;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getFittingSkuName() {
		return fittingSkuName;
	}

	public void setFittingSkuName(String fittingSkuName) {
		this.fittingSkuName = fittingSkuName;
	}

	public String getQuickCode() {
		return quickCode;
	}

	public void setQuickCode(String quickCode) {
		this.quickCode = quickCode;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

}