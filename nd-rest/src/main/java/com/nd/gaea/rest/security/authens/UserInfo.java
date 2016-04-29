/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.authens;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 根据UC中心定义的获取用户信息{ND共享平台用户中心API文档-1.6.1章节描述内容}定义的用户信息
 * @author johnny
 *
 */
public class UserInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7891245963510413200L;
	/**
	 * 用户的编号
	 */
	private String userId;
	/**
	 * 用户的名称
	 */
	private String userName;
	/**
	 * 用户的昵称
	 */
	private String nickName;
	/**
	 * 用户领域扩展属性
	 */
	private Map<String, Object> realmExinfo;
	/**
	 * 用户组织扩展属性
	 */
	
	private String userType;//请求类型，"Mac" or "Bearer"
	
	private Map<String, Object> orgExinfo;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Map<String, Object> getRealmExinfo() {
		return realmExinfo;
	}
	public void setRealmExinfo(Map<String, Object> realmExinfo) {
		this.realmExinfo = realmExinfo;
	}
	public Map<String, Object> getOrgExinfo() {
		return orgExinfo;
	}
	public void setOrgExinfo(Map<String, Object> orgExinfo) {
		this.orgExinfo = orgExinfo;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserCenterRoleDetails> getUserRoles(){
		List<UserCenterRoleDetails> roles = ((List<UserCenterRoleDetails>) SecurityContextHolder
				.getContext().getAuthentication().getAuthorities());
		return roles;
	}
	
	public WafUcCheckToken getUserToken(){
		WafUcCheckToken token = (WafUcCheckToken) SecurityContextHolder
				.getContext().getAuthentication().getCredentials();
		return token;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
}
