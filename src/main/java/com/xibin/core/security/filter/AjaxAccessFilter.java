package com.xibin.core.security.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class AjaxAccessFilter implements Filter {
	/**
	 * 需要排除（不拦截）的URL的正则表达式
	 */
	private Pattern excepUrlPattern;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Authentication");
		response.setHeader("Access-Control-Allow-Headers", "content-type");
		chain.doFilter(req, response);
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {

	}

}
