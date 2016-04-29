package com.nd.gaea.client.http;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

/**
 * 在 HttpContext 上加入拦截器
 *
 * @author vime
 * @since 0.9.6
 */
public class WafHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    public static final String REQUEST_INTERCEPTOR = "http.request-interceptor";
    public static final String RESPONSE_INTERCEPTOR = "http.response-interceptor";

    private HttpRequestInterceptor requestInterceptor;
    private HttpResponseInterceptor responseInterceptor;

    public WafHttpComponentsClientHttpRequestFactory(HttpClient httpClient, HttpRequestInterceptor requestInterceptor, HttpResponseInterceptor responseInterceptor) {
        super(httpClient);
        this.requestInterceptor = requestInterceptor;
        this.responseInterceptor = responseInterceptor;
    }

    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        HttpContext context = HttpClientContext.create();
        if (requestInterceptor != null)
            context.setAttribute(REQUEST_INTERCEPTOR, this.requestInterceptor);
        if (responseInterceptor != null)
            context.setAttribute(RESPONSE_INTERCEPTOR, this.responseInterceptor);
        return context;
    }
}
