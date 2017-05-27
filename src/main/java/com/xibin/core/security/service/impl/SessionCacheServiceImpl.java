package com.xibin.core.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.security.service.SessionCacheService;
import com.xibin.core.security.service.UserDetailsCacheService;
import com.xibin.core.security.threadcache.ThreadCacheAbstract;
@Service
public class SessionCacheServiceImpl extends ThreadCacheAbstract implements SessionCacheService {

private final String cacheName = "UserDetailsModel";
	
	@Autowired
	private UserDetailsCacheService userDetailsCacheService;
	
	@Override
	protected String getCacheName() {
		return cacheName;
	}

	@Override
	public void setLoginName(String loginName) {
		this.setCache(loginName);
		if (userDetailsCacheService.getUserDetails(loginName) == null) {
			userDetailsCacheService.setUserDetails(loginName, loginName);
		}
	}

	private String getLoginName() {
		return (String)this.getCache();
	}

	@Override
	public UserDetails getUserDetails() {
		return userDetailsCacheService.getUserDetails(getLoginName());
	}

}
