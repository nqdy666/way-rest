/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */

package com.nd.gaea.client.exception;

import org.springframework.http.HttpStatus;
/**
 * 捕获RestTemplate异常
 * @deprecated 请使用 {@link com.nd.gaea.client.WafResourceAccessException}
 * @author 110825
 * @since 0.9.5
 */
@Deprecated
public class WafRestTemplateException extends RuntimeException{

	private static final long serialVersionUID = -4226202947375867005L;
	
	private ErrorMessage errorMessage;
	private HttpStatus status;
	
	public WafRestTemplateException(ErrorMessage message,HttpStatus status) {
		super(message.getMessage());
		this.errorMessage = message;
		this.status = status;
	}
	
	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

	public HttpStatus getStatus() {
		return status;
	}

}
