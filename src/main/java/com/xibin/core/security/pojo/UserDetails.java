package com.xibin.core.security.pojo;

import java.io.Serializable;

public class UserDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5118240189727799180L;
	private int userId;
	//用户名
	private String userName;
	//公司ID
	private int companyId;
	private String companyName;
	//仓库ID
	private int warehouseId;
	private String warehouseName;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
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
