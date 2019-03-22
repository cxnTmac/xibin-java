package com.xibin.core.utils;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class JobForWXAccessTokenListener implements ApplicationListener<ContextRefreshedEvent> {
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// System.out.println("我的父容器为：" +
		// event.getApplicationContext().getParent());
		// System.out.println("初始化时我被调用了。");
		// if (event.getApplicationContext().getDisplayName().equals("Root
		// WebApplicationContext")) {
		// Runnable runnable = new Runnable() {
		// @Override
		// public void run() {
		// /**
		// * 定时设置accessToken
		// */
		// AccessTokenUtil.initAndSetAccessToken();
		// }
		// };
		//
		// ScheduledExecutorService service =
		// Executors.newSingleThreadScheduledExecutor();
		// service.scheduleAtFixedRate(runnable, 0, 7000, TimeUnit.SECONDS);
		// }
	}
}
