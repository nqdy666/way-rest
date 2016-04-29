/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.exceptions.extendExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import com.nd.gaea.rest.exceptions.messages.ErrorMessage;
/**
 * 在进行认证过程中，不通过配置形式封装异常消息体以及异常状态码。<br>
 * 此异常类来源于spring security的认证过程抛出的异常，认证过程通过AuthenticationEntryPoint捕获异常,<br>
 * AuthenticationEntryPoint 定义捕获异常的基类必须是：AuthenticationException ，所以我们定义Waf的<br>
 * 认证异常，并且实现WafExceptionSupport接口。
 * @author johnny
 * @deprecated 请使用 {@link com.nd.gaea.WafException}
 *
 */
@Deprecated
public class WafUcAuthenticationException extends AuthenticationException implements WafExceptionSupport{

	private ErrorMessage errorMessage;
	private HttpStatus status;
	
	public WafUcAuthenticationException(ErrorMessage message,HttpStatus status) {
		super(message.getMessage());
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}
	@Override
	public HttpStatus getStatus() {
		return status;
	}

	

}
