package com.nd.gaea.rest.exceptions.rest;

import com.nd.gaea.WafException;
import com.nd.gaea.client.exception.ErrorMessage;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认的 {@link WafException} 的异常处理对象
 *
 * @author vime
 * @since 0.9.6
 */
class WafExceptionRestErrorHandler extends AbstractRestErrorHandler {
    @Override
    public ResponseEntity<ResponseErrorMessage> process(Throwable throwable, HttpServletRequest request) {
        Assert.isInstanceOf(WafException.class, throwable);

        ResponseEntity<ErrorMessage> responseEntity = ((WafException) throwable).getResponseEntity();
        ErrorMessage body = responseEntity.getBody();

        ResponseErrorMessage responseErrorMessage = body.convert(throwable);
        //append stack trace
        responseErrorMessage.setDetail(appendStackTrace(body.getDetail(), throwable));
        updateRemoteErrorMessage(responseErrorMessage, request);

        return new ResponseEntity<>(responseErrorMessage, responseEntity.getHeaders(), responseEntity.getStatusCode());
    }
}
