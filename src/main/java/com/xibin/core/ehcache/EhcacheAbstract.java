/**
 * 
 */
package com.xibin.core.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

/**
 * @Description:
 * @author:Vincent.Zhang
 * 
 * 						<pre>
 * 版本号					修改人					修改说明
 * 2015年10月20日 .1			Vincent.Zhang    	新增
 *                       </pre>
 */
public abstract class EhcacheAbstract {

	protected abstract CacheConfiguration getCacheConfig();

	public Object getValue(String key) {
		if (null == key || "".equals(key)) {
			return null;
		}
		Element e = getCache().get(key);
		if (null == e) {
			return null;
		}
		return e.getObjectValue();
	}

	public Object getValueAndRemove(String key) {
		if (null == key || "".equals(key)) {
			return null;
		}
		Element e = null;
		synchronized (this) {
			e = getCache().get(key);
			getCache().remove(key);
		}
		if (null == e) {
			return null;
		}
		return e.getObjectValue();
	}

	public void setValue(String key, Object obj) {
		if (null == key || "".equals(key)) {
			return;
		}
		getCache().put(new Element(key, obj));
	}

	private Cache getCache() {
		if (null != _cache) {
			return _cache;
		} else {
			_cache = EhcacheBase.getCache(getCacheName());
			synchronized (this) {
				if (null == _cache) {
					_cache = EhcacheBase.createCache(getConfig());
				}
			}
			if (null == _cache) {
				throw new RuntimeException("Ehcache fail:01 ,Please check the cacheConfiguration!");
			}
			return _cache;
		}
	}

	private String getCacheName() {
		if (null != _cacheName && !"".equals(_cacheName)) {
			return _cacheName;
		} else {
			_cacheName = getConfig().getName();
			return _cacheName;
		}
	}

	private CacheConfiguration getConfig() {
		if (null != _config) {
			return _config;
		} else {
			_config = getCacheConfig();
			if (null == _config || null == _config.getName() || "".equals(_config.getName())) {
				throw new RuntimeException("Ehcache fail:02,Please check the cacheConfiguration!");
			}
			return _config;
		}
	}

	private String _cacheName;

	private CacheConfiguration _config;

	private Cache _cache;

}
