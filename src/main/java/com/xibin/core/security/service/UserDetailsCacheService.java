package com.xibin.core.security.service;

import com.xibin.core.security.pojo.UserDetails;

public interface UserDetailsCacheService {
	void setUserDetails(String key,String loginName);
	
	UserDetails getUserDetails(String key);
}
