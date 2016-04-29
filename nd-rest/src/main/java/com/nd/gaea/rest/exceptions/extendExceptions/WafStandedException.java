/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.exceptions.extendExceptions;

import com.nd.gaea.WafException;
import org.springframework.http.HttpStatus;

import com.nd.gaea.rest.exceptions.messages.ErrorMessage;

/**
 * Waf标准异常类，实现了WafExceptionSupport接口,同时继承RuntimeException<br>
 * <p/>
 * 此对象中封装了http响应码以及异常消息结构。构建此异常消息的时候，只需要设定异常响应码<br>
 * 和错误消息结构体即可。
 *
 * @author johnny
 * @deprecated 请使用 {@link WafException}
 */
@Deprecated
public class WafStandedException extends WafException implements WafExceptionSupport {

    private ErrorMessage errorMessage;

    public WafStandedException(ErrorMessage message, HttpStatus status) {
        super(message.getCode(), message.getMessage(), status);
        this.errorMessage = errorMessage;
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public HttpStatus getStatus() {
        return super.getResponseEntity().getStatusCode();
    }

}
