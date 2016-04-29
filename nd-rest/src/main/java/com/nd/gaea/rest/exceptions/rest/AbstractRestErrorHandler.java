package com.nd.gaea.rest.exceptions.rest;

import com.nd.gaea.client.exception.ResponseErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

/**
 * 对特定的异常进行处理，并以 JSON 响应异常信息
 *
 * @author vime
 * @since 0.9.6
 */
public abstract class AbstractRestErrorHandler {

    public ResponseEntity<ResponseErrorMessage> process(Throwable throwable, HttpServletRequest request) {
        Assert.notNull(throwable, "throwable");
        Assert.notNull(request, "request");

        ResponseErrorMessage errorMessage = getBody(throwable, request);
        HttpHeaders httpHandlers = getHttpHandlers(throwable, request);
        HttpStatus httpStatus = getHttpStatus(throwable, request);
        return new ResponseEntity<>(errorMessage, httpHandlers, httpStatus);
    }

    protected ResponseErrorMessage getBody(Throwable throwable, HttpServletRequest request) {
        ResponseErrorMessage errorMessage = new ResponseErrorMessage(throwable);
        errorMessage.setMessage(throwable.getLocalizedMessage());
        errorMessage.setDetail(appendStackTrace(null, throwable));
        errorMessage.setCode(getCode(throwable, request));
        updateRemoteErrorMessage(errorMessage, request);
        return errorMessage;
    }

    protected void updateRemoteErrorMessage(ResponseErrorMessage errorMessage, HttpServletRequest request) {
        errorMessage.setServerTime(new Date());
        errorMessage.setHostId(request.getServerName());
        errorMessage.setRequestId(UUID.randomUUID().toString());
    }

    protected HttpStatus getHttpStatus(Throwable throwable, HttpServletRequest request) {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    protected String getCode(Throwable throwable, HttpServletRequest request) {
        return "WAF/INTERNAL_SERVER_ERROR";
    }

    protected HttpHeaders getHttpHandlers(Throwable throwable, HttpServletRequest request) {
        return null;
    }

    protected String getStackTrace(Throwable throwable) {
        StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    protected String appendStackTrace(String detail, Throwable throwable) {
        if (throwable != null) {
            if (detail != null)
                detail += "\r\n";
            else
                detail = "";
            detail += "Stack trace:\r\n" + getStackTrace(throwable);
        }
        return detail;
    }
}
