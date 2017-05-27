package com.xibin.core.security.threadcache;

public abstract class ThreadCacheAbstract {

	/**
	 * @return the cacheName
	 */

	protected abstract String getCacheName();

	protected Object getCache() {
		try {
			return ThreadCacheStore.getCache(getCacheName());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void setCache(Object cacheObj) {
		try {
			ThreadCacheStore.setCache(getCacheName(), cacheObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void cleanCache() {
		try {
			ThreadCacheStore.setCache(getCacheName(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
