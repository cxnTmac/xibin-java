package com.xibin.core.security.service;

import com.xibin.core.security.pojo.UserDetails;

public interface SessionCacheService {
	void setLoginName(String loginName);
	
	UserDetails getUserDetails();
}
