package com.xibin.core.utils;

import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * @ClassName: ServletContextUtil
 * @author yangwl
 * @date 2016年5月11日 下午4:54:16
 * @Description: 全局缓存servletcontext
 */
public final class ServletContextUtil {

	private static ServletContext serveltContext = null;

	private ServletContextUtil() {
	};

	public synchronized static ServletContext get() {

		if (null == serveltContext) {
			WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			serveltContext = webApplicationContext.getServletContext();
		}
		return serveltContext;
	}
}