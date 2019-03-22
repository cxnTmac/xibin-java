package com.xibin.wms.entity;

import java.io.Serializable;

public class OutboundDetailQuickImportEntity implements Serializable {

	String fittingSkuCode;
	Double outboundPrice;
	Double outboundNum;

	public String getFittingSkuCode() {
		return fittingSkuCode;
	}

	public void setFittingSkuCode(String fittingSkuCode) {
		this.fittingSkuCode = fittingSkuCode;
	}

	public Double getOutboundPrice() {
		return outboundPrice;
	}

	public void setOutboundPrice(Double outboundPrice) {
		this.outboundPrice = outboundPrice;
	}

	public Double getOutboundNum() {
		return outboundNum;
	}

	public void setOutboundNum(Double outboundNum) {
		this.outboundNum = outboundNum;
	}

}
