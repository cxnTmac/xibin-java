package com.xibin.wms.entity;

import java.io.Serializable;
import java.security.KeyStore.PrivateKeyEntry;

public class InventoryUpdateEntity implements Serializable {
	
	private String actionCode;
	
	private String orderNo;
	
	private String orderType;
	
	private String lineNo;
	
	private String taskId;
	
	private String taskLineNo;
	
	private String skuCode;
	
	private String lotNum;
	
	private String locCode;
	//操作数
	private Double qtyOp;
	//操作前数
	private Double qtyOpBefore;
	//操作后数
	private Double qtyOpAfter;
	public String getActionCode() {
		return actionCode;
	}
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskLineNo() {
		return taskLineNo;
	}
	public void setTaskLineNo(String taskLineNo) {
		this.taskLineNo = taskLineNo;
	}
	public String getSkuCode() {
		return skuCode;
	}
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
	public String getLotNum() {
		return lotNum;
	}
	public void setLotNum(String lotNum) {
		this.lotNum = lotNum;
	}
	public String getLocCode() {
		return locCode;
	}
	public void setLocCode(String locCode) {
		this.locCode = locCode;
	}
	public Double getQtyOp() {
		return qtyOp;
	}
	public void setQtyOp(Double qtyOp) {
		this.qtyOp = qtyOp;
	}
	public Double getQtyOpBefore() {
		return qtyOpBefore;
	}
	public void setQtyOpBefore(Double qtyOpBefore) {
		this.qtyOpBefore = qtyOpBefore;
	}
	public Double getQtyOpAfter() {
		return qtyOpAfter;
	}
	public void setQtyOpAfter(Double qtyOpAfter) {
		this.qtyOpAfter = qtyOpAfter;
	}
	
	

}
