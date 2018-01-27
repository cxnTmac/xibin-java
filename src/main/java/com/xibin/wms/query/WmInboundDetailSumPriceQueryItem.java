package com.xibin.wms.query;

import java.io.Serializable;
import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmInboundDetailSumPriceQueryItem implements Serializable{
    
    private Double total;
    
    private String supplierCode;
    
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

   
}