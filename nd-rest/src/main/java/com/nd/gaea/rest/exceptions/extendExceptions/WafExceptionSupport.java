/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.exceptions.extendExceptions;

import com.nd.gaea.rest.exceptions.messages.ErrorMessage;
import org.springframework.http.HttpStatus;
/**
 * 定义waf的异常接口。<br>
 * 此异常接口主要在自定义异常的时候，可以通过此接口封装异常信息以及异常的http响应状态码<br>
 * @author johnny
 * @deprecated 请使用新的异常处理框架
 */
@Deprecated
public interface WafExceptionSupport {
	/**
	 * 获取异常信息的值对象{@link com.nd.gaea.client.exception.ErrorMessage}<br>
	 * @return
	 */
	public ErrorMessage getErrorMessage();

	/**
	 * 获取Http请求的状态码{@link HttpStatus}<br>
	 * @return
	 */
	public HttpStatus getStatus() ;
}
