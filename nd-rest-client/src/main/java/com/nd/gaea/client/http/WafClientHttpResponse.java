package com.nd.gaea.client.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author vime
 * @since 0.9.5
 */
class WafClientHttpResponse implements ClientHttpResponse {
    ClientHttpResponse innerResponse;
    HttpMethod httpMethod;
    URI uri;

    public WafClientHttpResponse(ClientHttpResponse innerResponse, HttpMethod httpMethod, URI uri) {
        Assert.notNull(innerResponse, "innerResponse cannot be null.");
        this.innerResponse = innerResponse;
        this.uri = uri;
        this.httpMethod = httpMethod;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return innerResponse.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return innerResponse.getRawStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return innerResponse.getStatusText();
    }

    @Override
    public void close() {
        innerResponse.close();
    }

    @Override
    public InputStream getBody() throws IOException {
        return innerResponse.getBody();
    }


    @Override
    public HttpHeaders getHeaders() {
        return innerResponse.getHeaders();
    }
}
