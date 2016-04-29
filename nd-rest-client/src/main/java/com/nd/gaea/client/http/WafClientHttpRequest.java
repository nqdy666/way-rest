package com.nd.gaea.client.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author vime
 * @since 0.9.5
 */
class WafClientHttpRequest implements ClientHttpRequest {
    ClientHttpRequest innerRequest;

    public WafClientHttpRequest(ClientHttpRequest innerRequest) {
        Assert.notNull(innerRequest,"innerRequest cannot be null.");
        this.innerRequest = innerRequest;
    }

    @Override
    public ClientHttpResponse execute() throws IOException {
        return new WafClientHttpResponse(innerRequest.execute(), innerRequest.getMethod(), innerRequest.getURI());
    }

    @Override
    public OutputStream getBody() throws IOException {
        return innerRequest.getBody();
    }

    @Override
    public HttpMethod getMethod() {
        return innerRequest.getMethod();
    }

    @Override
    public URI getURI() {
        return innerRequest.getURI();
    }

    @Override
    public HttpHeaders getHeaders() {
        return innerRequest.getHeaders();
    }
}