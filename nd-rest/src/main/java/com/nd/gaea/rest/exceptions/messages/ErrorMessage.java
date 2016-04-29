/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.exceptions.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

/**
 * 
 * 通用错误的封装类，封装错误信息的基本信息
 * @author johnny
 * @deprecated 请使用 {@link com.nd.gaea.client.exception.ErrorMessage}
 */
@Deprecated
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编码的Code
     */
    private String code;
    /**
     * 错误信息的message信息。
     */
    private String message;
    /**
     * 错误发生的业务领域
     */
    private String realm;
    /**
     * 发生错误的主机服务id号
     */
    private String hostId;
    /**
     * 请求资源的唯一id
     */
    private String requestId;
    /**
     * 服务器端错误发生的时间
     */
    private Date serverTime;
    /**
     * 再现说明错误类型的文档信息，可选
     */
    @JsonIgnore
    private URI type;
    
    /**
     * 异常堆栈信息
     */
    @JsonIgnore
    private String stackTrace;
    
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Date getServerTime() {
		return serverTime;
	}
	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}
	public URI getType() {
		return type;
	}
	public void setType(URI type) {
		this.type = type;
	}
	public ErrorMessage(String code, String message, String realm,
			String hostId, String requestId, Date serverTime, URI type) {
		super();
		this.code = code;
		this.message = message;
		this.realm = realm;
		this.hostId = hostId;
		this.requestId = requestId;
		this.serverTime = serverTime;
		this.type = type;
	}
	public ErrorMessage() {
		super();
	}
	public ErrorMessage(String code, String message) {
		this.message = message;
		this.code = code;
	}
	public ErrorMessage(String code) {
		this.code = code;
	}
}
