/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.client.exception;

import com.nd.gaea.I18NProvider;

import java.io.Serializable;

/**
 * 错误信息封装类
 *
 * @author 110825
 * @since 0.9.5
 */
public class ErrorMessage implements Serializable {
    private static final long serialVersionUID = -5401402542472113075L;

    private String code;
    private String message;
    private String detail;
    private ResponseErrorMessage cause;


    public ErrorMessage() {
    }

    public ErrorMessage(String code) {
        this(code, null, null);
    }

    public ErrorMessage(String code, String message) {
        this(code, message, null);
    }

    public ErrorMessage(String code, String message, String detail) {
        this(code, message, detail, null);
    }

    public ErrorMessage(String code, String message, String detail, ResponseErrorMessage cause) {
        this.message = message;
        this.code = code;
        this.detail = detail;
        this.cause = cause;
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

    public String getLocalizedMessage(){
        return I18NProvider.getString(message);
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * 获得远程服务返回的错误信息
     *
     * @return 远程该服务返回的错误信息
     * @since 0.9.6
     */
    public ResponseErrorMessage getCause() {
        return cause;
    }

    /**
     * 设置远程服务返回的错误信息
     *
     * @param cause 远程服务返回的错误信息
     * @since 0.9.6
     */
    public void setCause(ResponseErrorMessage cause) {
        this.cause = cause;
    }

    public ResponseErrorMessage convert(Throwable throwable) {
        ResponseErrorMessage responseErrorMessage = new ResponseErrorMessage(throwable);
        responseErrorMessage.setMessage(getLocalizedMessage());
        responseErrorMessage.setCode(code);
        responseErrorMessage.setDetail(detail);
        responseErrorMessage.setCause(cause);
        return responseErrorMessage;
    }
}
