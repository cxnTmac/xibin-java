package com.xibin.wms.pojo;

import java.io.Serializable;

public class PieChartForCurrentMonthSaleByType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private Double sales;

	private String fittingTypeCode;

	private String fittingTypeName;

	public Double getSales() {
		return sales;
	}

	public void setSales(Double sales) {
		this.sales = sales;
	}

	public String getFittingTypeCode() {
		return fittingTypeCode;
	}

	public void setFittingTypeCode(String fittingTypeCode) {
		this.fittingTypeCode = fittingTypeCode;
	}

	public String getFittingTypeName() {
		return fittingTypeName;
	}

	public void setFittingTypeName(String fittingTypeName) {
		this.fittingTypeName = fittingTypeName;
	}

}