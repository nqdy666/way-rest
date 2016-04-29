/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */

package com.nd.gaea.rest.exceptions.extendExceptions;

import com.nd.gaea.WafException;
import com.nd.gaea.rest.exceptions.messages.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * 定义简单的异常类对象。此对象实现了{@link WafExceptionSupport}接口<br>
 * 并且集成了RuntimeException。<br>
 * <p/>
 * 此异常类主要标注服务器内部的异常信息，所有的异常信息抛出后，其响应码统一为500<br>
 * 开发人员可以通过设置异常的编号和异常信息进行定义业务系统的异常信息。
 *
 * @author johnny
 * @deprecated 请使用 {@link WafException}
 */
@Deprecated
public class WafSimpleException extends WafException implements WafExceptionSupport {

    public WafSimpleException(String message) {
        super("WAF/INTERNAL_SERVER_ERROR", message);
    }

    public WafSimpleException(String code, String message) {
        super(code, message);
    }

    public WafSimpleException(HttpStatus status, String code, String message) {
        super(code, message, status);
    }

    @Override
    public ErrorMessage getErrorMessage() {
        ErrorMessage em = new ErrorMessage();
        em.setCode(super.getError().getCode());
        em.setMessage(super.getError().getMessage());
        return em;
    }

    @Override
    public HttpStatus getStatus() {
        return super.getResponseEntity().getStatusCode();
    }

}
