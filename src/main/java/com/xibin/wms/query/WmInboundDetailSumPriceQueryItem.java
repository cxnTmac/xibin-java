package com.xibin.wms.query;

import java.io.Serializable;

public class WmInboundDetailSumPriceQueryItem implements Serializable {

	private Double total;

	private String supplierCode;

	private String customerName;

	private Integer auxiId;

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
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