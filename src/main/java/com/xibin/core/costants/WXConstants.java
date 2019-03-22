package com.xibin.core.costants;

public final class WXConstants {
	// APPID
	public final static String APP_ID = "wxce961a786a558fb8";
	// APPSECRET
	public final static String APP_SECRET = "3c19ddf3c3889b16a1d4e8982b4cbecb";
	// 获取ACCESS_TOKEN的URL
	public final static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	// 获取JSAPI_TICKET的URL
	public final static String JSAPI_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
	// 获取登陆的OPPEN_ID的URL
	public final static String OPPEN_ID_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=APPSECRET&code=CODE&grant_type=authorization_code";
}
