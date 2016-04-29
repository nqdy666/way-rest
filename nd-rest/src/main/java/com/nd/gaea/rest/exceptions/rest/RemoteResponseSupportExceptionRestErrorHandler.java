package com.nd.gaea.rest.exceptions.rest;

import com.nd.gaea.client.RemoteResponseSupport;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * @author vime
 * @since 0.9.6
 */
class RemoteResponseSupportExceptionRestErrorHandler extends AbstractRestErrorHandler {

    private HttpStatus httpStatus;

    public RemoteResponseSupportExceptionRestErrorHandler(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public ResponseEntity<ResponseErrorMessage> process(Throwable throwable, HttpServletRequest request) {

        RemoteResponseSupport rrs = (RemoteResponseSupport) throwable;
        ResponseEntity<ResponseErrorMessage> remoteResponseEntity = rrs.getRemoteResponseEntity();
        ResponseErrorMessage errorMessage;
        if (remoteResponseEntity != null) {
            ResponseErrorMessage remoteBody = remoteResponseEntity.getBody();
            errorMessage = remoteBody.clone();
            errorMessage.setDetail(appendStackTrace(remoteBody.getDetail(), throwable));
            errorMessage.setCause(remoteBody);
            updateRemoteErrorMessage(errorMessage, request);
        } else {
            errorMessage = super.getBody(throwable, request);
        }
        HttpHeaders httpHandlers = getHttpHandlers(throwable, request);
        HttpStatus httpStatus = getHttpStatus(throwable, request);
        return new ResponseEntity<>(errorMessage, httpHandlers, httpStatus);
    }

    @Override
    protected String getCode(Throwable throwable, HttpServletRequest request) {
        return "WAF/" + httpStatus.getReasonPhrase().toUpperCase();
    }

    @Override
    protected HttpStatus getHttpStatus(Throwable throwable, HttpServletRequest request) {
        return httpStatus;
    }
}
