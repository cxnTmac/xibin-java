package com.xibin.wms.pojo;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmAssembleAlloc extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String orderNo;
	
	private String sLineNo;
	
    private String allocId;
	
	private String fittingSkuCode;
	
	private String status;
	
	private Integer allocNum;
	
	private String allocLoc;
	
	private String toLoc;
	
	private Integer pickOp;
	
	private Date pickTime;
	
	private String remark;
	
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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getsLineNo() {
		return sLineNo;
	}

	public void setsLineNo(String sLineNo) {
		this.sLineNo = sLineNo;
	}

	public String getAllocId() {
		return allocId;
	}

	public void setAllocId(String allocId) {
		this.allocId = allocId;
	}

	public String getFittingSkuCode() {
		return fittingSkuCode;
	}

	public void setFittingSkuCode(String fittingSkuCode) {
		this.fittingSkuCode = fittingSkuCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getAllocNum() {
		return allocNum;
	}

	public void setAllocNum(Integer allocNum) {
		this.allocNum = allocNum;
	}

	public String getAllocLoc() {
		return allocLoc;
	}

	public void setAllocLoc(String allocLoc) {
		this.allocLoc = allocLoc;
	}

	public String getToLoc() {
		return toLoc;
	}

	public void setToLoc(String toLoc) {
		this.toLoc = toLoc;
	}

	public Integer getPickOp() {
		return pickOp;
	}

	public void setPickOp(Integer pickOp) {
		this.pickOp = pickOp;
	}

	public Date getPickTime() {
		return pickTime;
	}

	public void setPickTime(Date pickTime) {
		this.pickTime = pickTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
