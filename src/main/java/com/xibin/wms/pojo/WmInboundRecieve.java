package com.xibin.wms.pojo;

import java.util.Date;

public class WmInboundRecieve {
    private Integer id;

    private String orderNo;

    private String lineNo;

    private String skuCode;

    private Double inboundNum;

    private Double inboundPrice;

    private String inboundLocCode;

    private String remark;

    private Integer creator;

    private Date createtime;

    private Integer modifier;

    private Date modifytime;

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
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo == null ? null : lineNo.trim();
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    public Double getInboundNum() {
        return inboundNum;
    }

    public void setInboundNum(Double inboundNum) {
        this.inboundNum = inboundNum;
    }

    public Double getInboundPrice() {
        return inboundPrice;
    }

    public void setInboundPrice(Double inboundPrice) {
        this.inboundPrice = inboundPrice;
    }

    public String getInboundLocCode() {
        return inboundLocCode;
    }

    public void setInboundLocCode(String inboundLocCode) {
        this.inboundLocCode = inboundLocCode == null ? null : inboundLocCode.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getModifier() {
        return modifier;
    }

    public void setModifier(Integer modifier) {
        this.modifier = modifier;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
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