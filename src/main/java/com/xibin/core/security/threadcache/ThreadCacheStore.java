
package com.xibin.core.security.threadcache;

import java.util.HashMap;


public class ThreadCacheStore {

	private static final ThreadLocal<HashMap<String, Object>> threadCacheMap = new ThreadLocal<HashMap<String, Object>>();

	public static void reset() {
		getThreadCacheMap().clear();
	}

	protected static void setCache(String key, Object value) {
		getThreadCacheMap().put(key, value);
	}

	protected static Object getCache(String key) {
		return getThreadCacheMap().get(key);
	}

	/**************************** private *********************************/

	// private static void setThreadCacheMap(HashMap<String, Object> map) {
	// threadCacheMap.set(map);
	// }

	private static HashMap<String, Object> getThreadCacheMap() {
		HashMap<String, Object> map = threadCacheMap.get();
		if (null == map) {
			threadCacheMap.set(new HashMap<String, Object>());
			map = threadCacheMap.get();
		}
		if (null == map) {
			throw new RuntimeException("threadCacheMap has not been initialized!");
		}
		return map;
	}

}
