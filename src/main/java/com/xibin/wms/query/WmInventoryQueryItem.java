package com.xibin.wms.query;

import java.io.Serializable;
import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmInventoryQueryItem implements Serializable{
    private Integer id;

    private String skuCode;
    
    private String skuName;
    
    private String modelCode;

    private String lot;

    private String locCode;

    private Double allocNum;

    private Double invNum;

    private Double invAvailableNum;

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

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
	
	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}
	
	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public String getLocCode() {
		return locCode;
	}

	public void setLocCode(String locCode) {
		this.locCode = locCode;
	}

	public Double getAllocNum() {
		return allocNum;
	}

	public void setAllocNum(Double allocNum) {
		this.allocNum = allocNum;
	}

	public Double getInvNum() {
		return invNum;
	}

	public void setInvNum(Double invNum) {
		this.invNum = invNum;
	}

	public Double getInvAvailableNum() {
		return invAvailableNum;
	}

	public void setInvAvailableNum(Double invAvailableNum) {
		this.invAvailableNum = invAvailableNum;
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