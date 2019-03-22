package com.xibin.core.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitializingBeanImpl implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("启动就执行！！！！！！！");
		AccessTokenUtil.initAndSetAccessToken();
	}

}
