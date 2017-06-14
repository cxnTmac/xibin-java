package com.xibin.wms.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class BdFittingSkuPic extends BaseModel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5251172328639564358L;

	private Integer id;

    private String fittingSkuCode;
    
    private String fittingSkuPicName;

    private String fittingSkuPicUrl;
    
    private String type;

    private Date createTime;

    private Integer creator;

    private Date modifyTime;

    private Integer modifier;

    private Integer recVer;

    private Integer companyId;
    
    private Integer width;
    
    private Integer height;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFittingSkuCode() {
		return fittingSkuCode;
	}

	public String getFittingSkuPicName() {
		return fittingSkuPicName;
	}

	public void setFittingSkuPicName(String fittingSkuPicName) {
		this.fittingSkuPicName = fittingSkuPicName;
	}

	public void setFittingSkuCode(String fittingSkuCode) {
		this.fittingSkuCode = fittingSkuCode;
	}

	public String getFittingSkuPicUrl() {
		return fittingSkuPicUrl;
	}

	public void setFittingSkuPicUrl(String fittingSkuPicUrl) {
		this.fittingSkuPicUrl = fittingSkuPicUrl;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
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

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
	
	
    
}