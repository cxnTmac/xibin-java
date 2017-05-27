package com.xibin.wms.pojo;

import java.util.Date;

import com.xibin.core.daosupport.BaseModel;

public class BdFittingType extends BaseModel{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6371167541577925861L;

	private Integer id;

    private String fittingTypeCode;

    private String fittingTypeName;

    private String fittingTypeStatus;

    private String fittingTypeRemark;

    private Date createTime;

    private Integer creator;

    private Date modifyTime;

    private Integer modifier;

    private Integer recVer;

    private Integer companyId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFittingTypeCode() {
        return fittingTypeCode;
    }

    public void setFittingTypeCode(String fittingTypeCode) {
        this.fittingTypeCode = fittingTypeCode == null ? null : fittingTypeCode.trim();
    }

    public String getFittingTypeName() {
        return fittingTypeName;
    }

    public void setFittingTypeName(String fittingTypeName) {
        this.fittingTypeName = fittingTypeName == null ? null : fittingTypeName.trim();
    }

    public String getFittingTypeStatus() {
        return fittingTypeStatus;
    }

    public void setFittingTypeStatus(String fittingTypeStatus) {
        this.fittingTypeStatus = fittingTypeStatus == null ? null : fittingTypeStatus.trim();
    }

    public String getFittingTypeRemark() {
        return fittingTypeRemark;
    }

    public void setFittingTypeRemark(String fittingTypeRemark) {
        this.fittingTypeRemark = fittingTypeRemark == null ? null : fittingTypeRemark.trim();
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
}