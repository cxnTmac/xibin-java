package com.xibin.wms.query;

import java.io.Serializable;
import java.util.Date;

public class WmOutboundDetailSaleHistoryQueryItem implements Serializable {

	private String fittingSkuName;

	private String modelCode;

	private String skuCode;

	private String buyerCode;

	private String customerName;

	private Date modifyTime;

	private Double outboundNum;

	private Double outboundPrice;

	public String getFittingSkuName() {
		return fittingSkuName;
	}

	public void setFittingSkuName(String fittingSkuName) {
		this.fittingSkuName = fittingSkuName;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
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

}