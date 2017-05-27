package com.xibin.core.security.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.jms.Session;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;
import com.xibin.core.costants.Constants;
import com.xibin.core.security.pojo.UserDetails;

public class LoginFilter implements Filter{
	/**
	 * 需要排除（不拦截）的URL的正则表达式
	 */
	private Pattern excepUrlPattern ;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();  
		String servletPath = request.getServletPath();// 请求URLs
		// 如果请求的路径与forwardUrl相同，或请求的路径是排除的URL时，则直接放行
		if (excepUrlPattern !=null && excepUrlPattern.matcher(servletPath).matches()) {
			chain.doFilter(req, res);
			return;
		}
		HttpSession session = request.getSession();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		if(null!=userDetails){
			chain.doFilter(req, res);
			return;
		}
		return;
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		// TODO Auto-generated method stub
		String casExcepUrlRegex = cfg.getServletContext().getInitParameter("excepUrlRegex");
		if (!StringUtils.isBlank(casExcepUrlRegex)) {
			excepUrlPattern = Pattern.compile(casExcepUrlRegex);
		}
	}

}
