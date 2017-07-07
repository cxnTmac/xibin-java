package com.xibin.wms.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmActTran extends BaseModel{
    private Integer id;

    private String tranId;

    private String tranType;

    private String orderType;

    private String orderNo;

    private String lineNo;

    private String taskId;
    
    private String taskLineNo;
    
    private int tranOp;
    
    private Date tranTime;
    
    private String fmSku;
    
    private String fmLot;
    
    private String fmLoc;
    
    private Double fmQtyOp;
    
    private Double fmQtyBefore;
    
    private Double fmQtyAfter;
    
    private String toSku;
    
    private String toLot;
    
    private String toLoc;
    
    private Double toQtyOp;
    
    private Double toQtyBefore;
    
    private Double toQtyAfter;
    
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

	public String getTranId() {
		return tranId;
	}

	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
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

	public int getTranOp() {
		return tranOp;
	}

	public void setTranOp(int tranOp) {
		this.tranOp = tranOp;
	}

	public Date getTranTime() {
		return tranTime;
	}

	public void setTranTime(Date tranTime) {
		this.tranTime = tranTime;
	}

	public String getFmSku() {
		return fmSku;
	}

	public void setFmSku(String fmSku) {
		this.fmSku = fmSku;
	}

	public String getFmLot() {
		return fmLot;
	}

	public void setFmLot(String fmLot) {
		this.fmLot = fmLot;
	}

	public String getFmLoc() {
		return fmLoc;
	}

	public void setFmLoc(String fmLoc) {
		this.fmLoc = fmLoc;
	}

	public Double getFmQtyOp() {
		return fmQtyOp;
	}

	public void setFmQtyOp(Double fmQtyOp) {
		this.fmQtyOp = fmQtyOp;
	}

	public Double getFmQtyBefore() {
		return fmQtyBefore;
	}

	public void setFmQtyBefore(Double fmQtyBefore) {
		this.fmQtyBefore = fmQtyBefore;
	}

	public Double getFmQtyAfter() {
		return fmQtyAfter;
	}

	public void setFmQtyAfter(Double fmQtyAfter) {
		this.fmQtyAfter = fmQtyAfter;
	}

	public String getToSku() {
		return toSku;
	}

	public void setToSku(String toSku) {
		this.toSku = toSku;
	}

	public String getToLot() {
		return toLot;
	}

	public void setToLot(String toLot) {
		this.toLot = toLot;
	}

	public String getToLoc() {
		return toLoc;
	}

	public void setToLoc(String toLoc) {
		this.toLoc = toLoc;
	}

	public Double getToQtyOp() {
		return toQtyOp;
	}

	public void setToQtyOp(Double toQtyOp) {
		this.toQtyOp = toQtyOp;
	}

	public Double getToQtyBefore() {
		return toQtyBefore;
	}

	public void setToQtyBefore(Double toQtyBefore) {
		this.toQtyBefore = toQtyBefore;
	}

	public Double getToQtyAfter() {
		return toQtyAfter;
	}

	public void setToQtyAfter(Double toQtyAfter) {
		this.toQtyAfter = toQtyAfter;
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
}