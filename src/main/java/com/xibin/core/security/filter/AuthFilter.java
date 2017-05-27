package com.xibin.core.security.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;




public class AuthFilter extends SessionFilterAbstract{
	
	private static final String KEY = "CURRENTUSER";
	@Override
	public void init(FilterConfig cfg) throws ServletException {
		super.init(cfg);
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(cfg.getServletContext());
		//orgOrgDeptUserUnionManager = (OrgOrgDeptUserUnionManager) ac.getBean("orgOrgDeptUserUnionManager");
	}

	@Override
	public boolean requestIsLegal(ServletRequest req, ServletResponse res) {
		// TODO:校验登陆用户是否有权限访问该URL
		// 如果不合法，重定向，并返回到false
		// 合法，设置当前系统session，并返回true
		setSession(req, res);
		return true;
	}
	
	private void setSession(ServletRequest req, ServletResponse res){
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		// _const_cas_assertion_是CAS中存放登录用户名的session标志
		String userName = (String) httpRequest.getSession().getAttribute("KEY");
		
		
	}

}
