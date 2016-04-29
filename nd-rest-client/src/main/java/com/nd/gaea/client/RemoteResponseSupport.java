package com.nd.gaea.client;

import com.nd.gaea.client.exception.ResponseErrorMessage;
import org.springframework.http.ResponseEntity;

/**
 * @author vime
 * @since 0.9.6
 */
public interface RemoteResponseSupport {
    public ResponseEntity<ResponseErrorMessage> getRemoteResponseEntity();
}
