package com.xibin.core.security.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.security.service.SessionCacheService;

public abstract class SessionFilterAbstract implements Filter{
	/**
	 * 需要排除（不拦截）的URL的正则表达式
	 */
	private Pattern excepUrlPattern ;
	private static final String KEY = "CURRENTUSER";
	private SessionCacheService sessionCacheService;//线程级
	/**
	 * 请求是否合法性判断
	 * @return boolean
	 */
	public abstract boolean requestIsLegal(ServletRequest req, ServletResponse res);
	
	@Override
	public void init(FilterConfig cfg) throws ServletException {
		String casExcepUrlRegex = cfg.getServletContext().getInitParameter("casExcepUrlRegex");
		if (!StringUtils.isBlank(casExcepUrlRegex)) {
			excepUrlPattern = Pattern.compile(casExcepUrlRegex);
		}
		
		
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(cfg.getServletContext());
		sessionCacheService = (SessionCacheService) ac.getBean("sessionCacheManager");
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String servletPath = request.getServletPath();// 请求URL

		// 如果请求的路径与forwardUrl相同，或请求的路径是排除的URL时，则直接放行
		if (excepUrlPattern !=null && excepUrlPattern.matcher(servletPath).matches()) {
			chain.doFilter(req, res);
			return;
		}
		
		// 从session线程中获取用户信息
		UserDetails userDetails = sessionCacheService.getUserDetails();
		// 1、检查Session是否为空
		if (userDetails != null) {
			if (requestIsLegal(req,res)) {
				chain.doFilter(req, res);
			}
		}
	}

	@Override
	public void destroy() {
	}
}
