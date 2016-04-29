package com.nd.gaea.client.http;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.nd.gaea.WafProperties;

/**
 * @author vime
 * @since 0.9.6
 */
public class WafRestTemplateBuilder {
    private HttpComponentsClientHttpRequestFactory requestFactory;
    private WafRestTemplate wafRestTemplate;
    private HttpClient httpClient;

    public WafRestTemplate build(int connectTimeout, int socketTimeout) {
        return build(WafHttpClientSingleton.getInstance().getHttpClient(), null, null, connectTimeout, socketTimeout);
    }
    
    public WafRestTemplate build(int connectTimeout, int socketTimeout, int retryCount) {
        return build(WafHttpClientSingleton.getInstance().getHttpClient(retryCount), null, null, connectTimeout, socketTimeout);
    }
    
    public WafRestTemplate build(int connectTimeout, int socketTimeout, int retryCount, Collection<Class<? extends IOException>> clazzes) {
        return build(WafHttpClientSingleton.getInstance().getHttpClient(retryCount, clazzes), null, null, connectTimeout, socketTimeout);
    }

    public WafRestTemplate build(HttpClient httpClient) {
        return build(httpClient, null, null);
    }

    public WafRestTemplate build(HttpClient httpClient, HttpRequestInterceptor requestInterceptor, HttpResponseInterceptor responseInterceptor) {
        return build(
                httpClient,
                requestInterceptor,
                responseInterceptor,
                WafProperties.getPropertyForInteger(WafHttpClient.WAF_CLIENT_CONNECT_TIMEOUT),
                WafProperties.getPropertyForInteger(WafHttpClient.WAF_CLIENT_SOCKET_TIMEOUT));
    }


    public WafRestTemplate build(HttpClient httpClient, int connectTimeout, int socketTimeout) {
        return build(httpClient, null, null, connectTimeout, socketTimeout);
    }

    public WafRestTemplate build(HttpRequestInterceptor requestInterceptor,
                                 HttpResponseInterceptor responseInterceptor) {
        return build(
                requestInterceptor,
                responseInterceptor,
                WafProperties.getPropertyForInteger(WafHttpClient.WAF_CLIENT_CONNECT_TIMEOUT),
                WafProperties.getPropertyForInteger(WafHttpClient.WAF_CLIENT_SOCKET_TIMEOUT));
    }

    public WafRestTemplate build(HttpRequestInterceptor requestInterceptor,
                                 HttpResponseInterceptor responseInterceptor,
                                 int connectTimeout,
                                 int socketTimeout) {
        return build(WafHttpClientSingleton.getInstance().getHttpClient(), requestInterceptor, responseInterceptor, connectTimeout, socketTimeout);
    }

    public WafRestTemplate build(HttpClient httpClient, HttpRequestInterceptor requestInterceptor,
                                 HttpResponseInterceptor responseInterceptor,
                                 int connectTimeout,
                                 int socketTimeout) {
        if (connectTimeout < 0)
            throw new IllegalArgumentException("Connect timeout value is illegal, must be >=0");
        if (socketTimeout < 0)
            throw new IllegalArgumentException("Socket timeout value is illegal, must be >=0");

        this.httpClient = httpClient;
        requestFactory = buildRequestFactory(httpClient, requestInterceptor, responseInterceptor, connectTimeout, socketTimeout);
        wafRestTemplate = buildWafRestTemplate(requestFactory);
        return wafRestTemplate;
    }

    public HttpComponentsClientHttpRequestFactory getRequestFactory() {
        return requestFactory;
    }

    public WafRestTemplate getWafRestTemplate() {
        return wafRestTemplate;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    protected WafRestTemplate buildWafRestTemplate(HttpComponentsClientHttpRequestFactory requestFactory) {
        return new WafRestTemplate(requestFactory);
    }

    protected HttpComponentsClientHttpRequestFactory buildRequestFactory(
            HttpClient httpClient,
            HttpRequestInterceptor requestInterceptor,
            HttpResponseInterceptor responseInterceptor,
            int connectTimeout,
            int socketTimeout) {
        HttpComponentsClientHttpRequestFactory requestFactory = new WafHttpComponentsClientHttpRequestFactory(httpClient, requestInterceptor, responseInterceptor);

        requestFactory.setConnectTimeout(connectTimeout);//设置连接主机超时
        requestFactory.setReadTimeout(socketTimeout);//设置从主机读取数据超时
        return requestFactory;
    }
}
