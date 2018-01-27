package com.xibin.wms.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class WmAssembleSDetail extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String orderNo;
	
	private String fLineNo;
	
	private String lineNo;
	
	private String fittingSkuCode;
	
	private String status;
	
	private Integer num;
	
	private Integer allocNum;
	
	private Integer pickNum;
	
	private Integer assembleNum;
	
	private String assembleLoc;
	
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

	public String getfLineNo() {
		return fLineNo;
	}

	public void setfLineNo(String fLineNo) {
		this.fLineNo = fLineNo;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
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

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Integer getAllocNum() {
		return allocNum;
	}

	public void setAllocNum(Integer allocNum) {
		this.allocNum = allocNum;
	}

	public Integer getPickNum() {
		return pickNum;
	}

	public void setPickNum(Integer pickNum) {
		this.pickNum = pickNum;
	}

	public Integer getAssembleNum() {
		return assembleNum;
	}

	public void setAssembleNum(Integer assembleNum) {
		this.assembleNum = assembleNum;
	}

	public String getAssembleLoc() {
		return assembleLoc;
	}

	public void setAssembleLoc(String assembleLoc) {
		this.assembleLoc = assembleLoc;
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