package com.xibin.core.security.pojo;

import java.io.Serializable;

public class UserDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5118240189727799180L;
	private Integer userId;
	//用户名
	private String userName;
	//公司ID
	private Integer companyId;
	private String companyName;
	//仓库ID
	private Integer warehouseId;
	private String warehouseName;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	
}
