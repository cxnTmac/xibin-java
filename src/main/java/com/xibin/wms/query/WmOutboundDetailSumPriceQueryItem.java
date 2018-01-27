package com.xibin.wms.query;

import java.io.Serializable;
import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmOutboundDetailSumPriceQueryItem implements Serializable{
    
    private Double total;
    
    private String buyerCode;
    
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

   
}