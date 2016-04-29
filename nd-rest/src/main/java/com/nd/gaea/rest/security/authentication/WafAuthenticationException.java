package com.nd.gaea.rest.security.authentication;

import com.nd.gaea.client.exception.ResponseErrorMessage;
import com.nd.gaea.client.RemoteResponseSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;

/**
 * @author vime
 * @since 0.9.6
 */
public class WafAuthenticationException extends AuthenticationException implements RemoteResponseSupport {

    private final ResponseEntity<ResponseErrorMessage> remoteResponseEntity;


    public WafAuthenticationException(String msg, Throwable t) {
        this(msg, t, null);
    }


    public WafAuthenticationException(String msg) {
        this(msg, null, null);
    }

    public WafAuthenticationException(String msg, Throwable t, ResponseEntity<ResponseErrorMessage> remoteResponseEntity) {
        super(msg, t);
        this.remoteResponseEntity = remoteResponseEntity;
    }

    @Override
    public ResponseEntity<ResponseErrorMessage> getRemoteResponseEntity() {
        return remoteResponseEntity;
    }
}
