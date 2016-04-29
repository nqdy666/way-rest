package com.nd.gaea.client;

import com.nd.gaea.SR;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

/**
 * 标识访问远程服务器提供 RESTful api 资源过程中产生异常
 *
 * @author vime
 * @since 0.9.5
 */
public class WafResourceAccessException extends RuntimeException implements RemoteResponseSupport {
    private static final long serialVersionUID = 8905820435703258732L;

    private ResponseEntity<ResponseErrorMessage> remoteResponseEntity;
    private URI uri;
    private HttpMethod httpMethod;

    public WafResourceAccessException(HttpMethod httpMethod, URI uri, ResponseEntity<ResponseErrorMessage> remoteResponseEntity) {
        super(SR.format("waf.er.resourceAccess.exception", httpMethod, uri, remoteResponseEntity.getBody().getCode(), remoteResponseEntity.getBody().getMessage()));
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.remoteResponseEntity = remoteResponseEntity;
    }

    public String getDetail() {
        StringBuilder sb = new StringBuilder("远程 REST api \"" + httpMethod + " " + uri + "\" 返回错误信息:\r\n");
        sb.append("error_code: ");
        sb.append(remoteResponseEntity.getBody().getCode());
        sb.append(", error_message: ");
        sb.append(remoteResponseEntity.getBody().getMessage());
        sb.append(", http_status: ");
        sb.append(remoteResponseEntity.getStatusCode());
        sb.append(", http_headers:");
        if (remoteResponseEntity.getHeaders() == null) {
            sb.append("[]");
        } else {
            sb.append("[");
            boolean first = true;
            for (Map.Entry<String, String> entry : remoteResponseEntity.getHeaders().toSingleValueMap().entrySet()) {
                if (!first)
                    sb.append(", ");
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(entry.getValue());
                first = false;
            }
            sb.append("]");
        }
        return sb.toString();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public ResponseEntity<ResponseErrorMessage> getRemoteResponseEntity() {
        return remoteResponseEntity;
    }
}
