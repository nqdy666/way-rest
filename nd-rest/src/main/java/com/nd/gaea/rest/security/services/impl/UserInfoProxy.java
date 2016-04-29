package com.nd.gaea.rest.security.services.impl;

import com.nd.gaea.rest.security.authens.UserInfo;
import com.nd.gaea.rest.security.services.WafUserDetailsService;

import java.util.Map;

/**
 * userInfo代理类
 * @author 110825
 * @since 0.9.5
 */
class UserInfoProxy extends UserInfo {

	private static final long serialVersionUID = -93919994657753490L;
	
	private boolean flag;
	private String userId;
	private String realm;
	private UserInfo userInfo;
	private WafUserDetailsService wafUserDetailsService;
	
	public UserInfoProxy(String userId, String realm, WafUserDetailsService wafUserDetailsService){
		this.userId = userId;
		this.realm = realm;
		this.wafUserDetailsService = wafUserDetailsService;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public String getUserName() {
		if (!flag) {
			loadUserInfo();
		}
		return userInfo.getUserName();
	}

	@Override
	public String getNickName() {
		if (!flag) {
			loadUserInfo();
		}
		return userInfo.getNickName();
	}

	@Override
	public Map<String, Object> getRealmExinfo() {
		if (!flag) {
			loadUserInfo();
		}
		return userInfo.getRealmExinfo();
	}

	@Override
	public Map<String, Object> getOrgExinfo() {
		if (!flag) {
			loadUserInfo();
		}
		return userInfo.getOrgExinfo();
	}
	
	
	public void setUserId(String userId) {
		if (!flag) {
			loadUserInfo();
		}
		this.userInfo.setUserId(userId);
	}

	public void setUserName(String userName) {
		if (!flag) {
			loadUserInfo();
		}
		this.userInfo.setUserName(userName);
	}

	public void setNickName(String nickName) {
		if (!flag) {
			loadUserInfo();
		}
		this.userInfo.setNickName(nickName);
	}

	public void setRealmExinfo(Map<String, Object> realmExinfo) {
		if (!flag) {
			loadUserInfo();
		}
		this.userInfo.setRealmExinfo(realmExinfo);
	}

	public void setOrgExinfo(Map<String, Object> orgExinfo) {
		if (!flag) {
			loadUserInfo();
		}
		this.userInfo.setOrgExinfo(orgExinfo);
	}
	
	
	public void loadUserInfo(){
		//发送获取userInfo的http请求
		userInfo = wafUserDetailsService.getUserInfo(userId, realm);
		flag = true;
	}
}
