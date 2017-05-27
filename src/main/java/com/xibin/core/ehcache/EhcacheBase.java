/**
 * 
 */
package com.xibin.core.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;


public class EhcacheBase {

	private static final CacheManager ehcacheManager = new CacheManager();

	protected static Cache createCache(CacheConfiguration config) {
		if (ehcacheManager.cacheExists(config.getName())) {
			return ehcacheManager.getCache(config.getName());
		} else {
			Cache cache = new Cache(config);
			ehcacheManager.addCache(cache);
			return cache;
		}
	}

	protected static Cache getCache(String name) {
		if (ehcacheManager.cacheExists(name)) {
			return ehcacheManager.getCache(name);
		} else {
			return null;
		}
	}

}
