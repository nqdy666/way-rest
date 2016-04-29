/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */

package com.nd.gaea.rest.security.authens;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 通过Mac进行验证有效性，返回验证的结果封装
 * @author johnny
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class WafUcCheckToken implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5737546567777696640L;
	/**
	 * 认证后的用户id
	 */
	private String userId;
	/**
	 * 验证后的token
	 */
	private String accessToken;
	/**
	 * 过期刷新用的token
	 */
	private String refreshToken;
	
	/**
	 * 服务器端通信安全验证的token
	 */
	private String bearerToken;
	/**
	 * 过期时间
	 */
	private Date expiresAt;
	/**
	 * 服务器返回时间
	 */
	private Date serverTime;
	/**
	 * hmac 的密钥
	 */
	private String macKey;
	
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public Date getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}
	public Date getServerTime() {
		return serverTime;
	}
	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}
	public String getBearerToken() {
		return bearerToken;
	}
	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}
	public String getMacKey() {
		return macKey;
	}
	public void setMacKey(String macKey) {
		this.macKey = macKey;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	/**
	 * 判断是否过期
	 */
    public boolean isExpire() {
        Date start = new Date();
        Date end = getExpiresAt();
        return (end.getTime() - start.getTime()) < 0L;
    }
	
}
