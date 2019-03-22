package com.xibin.wms.query;

import java.io.Serializable;

public class WmOutboundDetailSumPriceQueryItem implements Serializable {

	private Double total;

	private String buyerCode;

	private String customerName;

	private Integer auxiId;

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public Integer getAuxiId() {
		return auxiId;
	}

	public void setAuxiId(Integer auxiId) {
		this.auxiId = auxiId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

}