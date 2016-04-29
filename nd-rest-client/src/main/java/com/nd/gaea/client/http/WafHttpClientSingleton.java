package com.nd.gaea.client.http;


import java.io.IOException;
import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;


/**
 * @author vime
 * @since 0.9.6
 */
public final class WafHttpClientSingleton {
    private static final WafHttpClientSingleton instance = new WafHttpClientSingleton();
    private WafHttpClientBuilder wafHttpClientBuilder;

    private WafHttpClientSingleton() {
        wafHttpClientBuilder = new WafHttpClientBuilder();
        wafHttpClientBuilder.build();
    }

    public static WafHttpClientSingleton getInstance() {
        return instance;
    }

    public HttpClient getHttpClient() {
        return wafHttpClientBuilder.getHttpClient();
    }
    
    public HttpClient getHttpClient(int retryCount) {
        return wafHttpClientBuilder.getHttpClient(retryCount);
    }
    
    public HttpClient getHttpClient(int retryCount, Collection<Class<? extends IOException>> clazzes) {
        return wafHttpClientBuilder.getHttpClient(retryCount, clazzes);
    }

    public HttpClientConnectionManager getConnectionManager() {
        return wafHttpClientBuilder.getConnectionManager();
    }


}
