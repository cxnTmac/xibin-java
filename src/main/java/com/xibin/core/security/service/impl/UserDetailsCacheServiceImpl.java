package com.xibin.core.security.service.impl;

import org.springframework.stereotype.Service;

import com.xibin.core.ehcache.EhcacheAbstract;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.security.service.UserDetailsCacheService;

import net.sf.ehcache.config.CacheConfiguration;
@Service
public class UserDetailsCacheServiceImpl extends EhcacheAbstract implements UserDetailsCacheService{

	@Override
	public void setUserDetails(String key, String loginName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserDetails getUserDetails(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CacheConfiguration getCacheConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
