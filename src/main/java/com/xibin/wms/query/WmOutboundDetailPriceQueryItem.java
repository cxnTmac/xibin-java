package com.xibin.wms.query;

import java.io.Serializable;
import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmOutboundDetailPriceQueryItem implements Serializable{
	
    private Double outboundPrice;
    


    private Date modifyTime;



	public Double getOutboundPrice() {
		return outboundPrice;
	}



	public void setOutboundPrice(Double outboundPrice) {
		this.outboundPrice = outboundPrice;
	}



	public Date getModifyTime() {
		return modifyTime;
	}



	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}


	
	

	
}