package com.nd.gaea.rest.exceptions.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * @author vime
 * @since 0.9.6
 */
public class DefaultRestErrorHandler extends AbstractRestErrorHandler {

    private HttpStatus httpStatus;
    private String code;
    private HttpHeaders httpHeaders;

    public DefaultRestErrorHandler(HttpStatus httpStatus) {
        this(httpStatus, "WAF/" + httpStatus.getReasonPhrase().toUpperCase().replace(" ", "_"));
    }

    public DefaultRestErrorHandler(HttpStatus httpStatus, String code) {
        this(httpStatus, code, null);
    }

    public DefaultRestErrorHandler(HttpStatus httpStatus, String code, HttpHeaders httpHeaders) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.httpHeaders = httpHeaders;
    }

    @Override
    protected HttpStatus getHttpStatus(Throwable throwable, HttpServletRequest request) {
        return httpStatus;
    }

    @Override
    protected String getCode(Throwable throwable, HttpServletRequest request) {
        return code;
    }

    @Override
    protected HttpHeaders getHttpHandlers(Throwable throwable, HttpServletRequest request) {
        return httpHeaders;
    }
}
