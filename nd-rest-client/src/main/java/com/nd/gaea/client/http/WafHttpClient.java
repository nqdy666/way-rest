package com.nd.gaea.client.http;


import com.nd.gaea.WafProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * http请求工具类
 *
 * @author vime
 * @since 0.9.5
 */
public class WafHttpClient {
    public static final String WAF_CLIENT_CONNECT_TIMEOUT = "waf.client.connectTimeout";
    public static final String WAF_CLIENT_SOCKET_TIMEOUT = "waf.client.socketTimeout";
    /**
     * 设置连接池的最大连接数，所有的 WafHttpClient 实例共享同一个连接池
     *
     * @since 0.9.6
     */
    public static final String WAF_CLIENT_MAX_TOTAL = "waf.client.maxTotal";

    /**
     * 设置某个站点最大连接个数
     *
     * @since 0.9.6
     */
    public static final String WAF_CLIENT_MAX_PER_ROUTE = "waf.client.maxPerRoute";

    protected static final String WAF_CLIENT_CONNECT_TIMEOUT_VALUE = "30000";
    protected static final String WAF_CLIENT_SOCKET_TIMEOUT_VALUE = "10000";
    protected static final String WAF_CLIENT_MAX_TOTAL_VALUE = "1000";
    protected static final String WAF_CLIENT_MAX_PER_ROUTE_VALUE = "500";
    
    
    protected static int WAF_CLIENT_CONNECT_TIMEOUT_INT_VALUE;
    protected static int WAF_CLIENT_SOCKET_TIMEOUT_INT_VALUE;
    
    protected static final Logger log = LoggerFactory.getLogger(WafHttpClient.class);
    protected WafRestTemplate restTemplate;

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.setProperty(WafHttpClient.WAF_CLIENT_CONNECT_TIMEOUT, WAF_CLIENT_CONNECT_TIMEOUT_VALUE);
        defaultProperties.setProperty(WafHttpClient.WAF_CLIENT_SOCKET_TIMEOUT, WAF_CLIENT_SOCKET_TIMEOUT_VALUE);
        defaultProperties.setProperty(WafHttpClient.WAF_CLIENT_MAX_TOTAL, WAF_CLIENT_MAX_TOTAL_VALUE);
        defaultProperties.setProperty(WafHttpClient.WAF_CLIENT_MAX_PER_ROUTE, WAF_CLIENT_MAX_PER_ROUTE_VALUE);
        
        WAF_CLIENT_CONNECT_TIMEOUT_INT_VALUE = WafProperties.getPropertyForInteger(WAF_CLIENT_CONNECT_TIMEOUT);
        WAF_CLIENT_SOCKET_TIMEOUT_INT_VALUE = WafProperties.getPropertyForInteger(WAF_CLIENT_SOCKET_TIMEOUT);
    }

    public WafHttpClient() {
        this(WAF_CLIENT_CONNECT_TIMEOUT_INT_VALUE, WAF_CLIENT_SOCKET_TIMEOUT_INT_VALUE);
    }
    
    public WafHttpClient(int retryCount) {
        this(WAF_CLIENT_CONNECT_TIMEOUT_INT_VALUE, WAF_CLIENT_SOCKET_TIMEOUT_INT_VALUE, retryCount);
    }
    
    public WafHttpClient(int retryCount, Collection<Class<? extends IOException>> clazzes) {
        this(WAF_CLIENT_CONNECT_TIMEOUT_INT_VALUE, WAF_CLIENT_SOCKET_TIMEOUT_INT_VALUE, retryCount, clazzes);
    }

    /**
     * 初始化WafHttpClient对象
     *
     * @param connectTimeout 连接超时时间（毫秒），默认 5000 ms
     * @param socketTimeout  socket读写数据超时时间（毫秒），默认 10000 ms
     */
    public WafHttpClient(int connectTimeout, int socketTimeout) {
       if (connectTimeout < 0)
           throw new IllegalArgumentException("Connect timeout value is illegal, must be >=0");
       if (socketTimeout < 0)
           throw new IllegalArgumentException("Socket timeout value is illegal, must be >=0");
       WafRestTemplateBuilder wafRestTemplateBuilder = new WafRestTemplateBuilder();
       restTemplate = wafRestTemplateBuilder.build(connectTimeout, socketTimeout);
    }
    
    /**
     * 根据配置提供拥有重试能力的HttpClient
     * @param connectTimeout
     * @param socketTimeout
     * @param retryCount
     */
    public WafHttpClient(int connectTimeout, int socketTimeout, int retryCount) {
       this(connectTimeout, socketTimeout, retryCount, new ArrayList<Class<? extends IOException>>());
    }
    
    /**
     * 根据配置提供拥有重试能力的HttpClient
     * Create the request retry handler using the specified IOException classes
     *
     * @param connectTimeout
     * @param socketTimeout
     * @param retryCount how many times to retry; 0 means no retries
     * @param requestSentRetryEnabled true if it's OK to retry requests that have been sent
     * @param clazzes the IOException types that should not be retried
     */
    public WafHttpClient(int connectTimeout, int socketTimeout, int retryCount, Collection<Class<? extends IOException>> clazzes) {
        if (connectTimeout < 0)
            throw new IllegalArgumentException("Connect timeout value is illegal, must be >=0");
        if (socketTimeout < 0)
            throw new IllegalArgumentException("Socket timeout value is illegal, must be >=0");
        WafRestTemplateBuilder wafRestTemplateBuilder = new WafRestTemplateBuilder();
        restTemplate = wafRestTemplateBuilder.build(connectTimeout, socketTimeout, retryCount, clazzes);
    }

    public WafHttpClient(WafRestTemplate restTemplate) {
        Assert.notNull(restTemplate);
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
    
    void setRestTemplate(WafRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected HttpHeaders mergerHeaders(HttpHeaders headers) {
        return headers;
    }

    //region post

    /**
     * 发送 post 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @since 0.9.5
     */
    public <T> T postForObject(String url, Object requestBody, Type responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        return executeForObject(url, HttpMethod.POST, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 post 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> postForEntity(String url, Object requestBody, Type responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        return executeForEntity(url, HttpMethod.POST, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 post 请求
     *
     * @param url
     * @param requestBody
     * @param uriVariables
     */
    public void post(String url, Object requestBody, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        execute(url, HttpMethod.POST, httpEntity, uriVariables);
    }

    /**
     * 发送 post 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T postForObject(String url, Object requestBody, Type responseType, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        return executeForObject(url, HttpMethod.POST, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 post 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> postForEntity(String url, Object requestBody, Type responseType, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        return executeForEntity(url, HttpMethod.POST, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 post 请求
     *
     * @param url
     * @param requestBody
     * @param uriVariables
     */
    public void post(String url, Object requestBody, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        execute(url, HttpMethod.POST, httpEntity, uriVariables);
    }
    //endregion

    //region get

    /**
     * 发送 get 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T getForObject(String url, Type responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        return executeForObject(url, HttpMethod.GET, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 get 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> getForEntity(String url, Type responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        return executeForEntity(url, HttpMethod.GET, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 get 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T getForObject(String url, Type responseType, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        return executeForObject(url, HttpMethod.GET, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 get 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> getForEntity(String url, Type responseType, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        return executeForEntity(url, HttpMethod.GET, httpEntity, responseType, uriVariables);
    }
    //endregion

    //region put

    /**
     * 发送 put 请求
     *
     * @param url
     * @param requestBody
     * @param uriVariables
     */
    public void put(String url, Object requestBody, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        execute(url, HttpMethod.PUT, httpEntity, uriVariables);
    }

    /**
     * 发送 put 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T putForObject(String url, Object requestBody, Type responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        return executeForObject(url, HttpMethod.PUT, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 put 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> putForEntity(String url, Object requestBody, Type responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        return executeForEntity(url, HttpMethod.PUT, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 put 请求
     *
     * @param url
     * @param requestBody
     * @param uriVariables
     */
    public void put(String url, Object requestBody, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        execute(url, HttpMethod.PUT, httpEntity, uriVariables);
    }

    /**
     * 发送 put 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T putForObject(String url, Object requestBody, Type responseType, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        return executeForObject(url, HttpMethod.PUT, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 put 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> putForEntity(String url, Object requestBody, Type responseType, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(requestBody);
        return executeForEntity(url, HttpMethod.PUT, httpEntity, responseType, uriVariables);
    }
    //endregion

    //region delete

    /**
     * 发送 delete 请求
     *
     * @param url
     * @param uriVariables
     */
    public void delete(String url, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        execute(url, HttpMethod.DELETE, httpEntity, uriVariables);
    }

    /**
     * 发送 delete 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T deleteForObject(String url, Type responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        return executeForObject(url, HttpMethod.DELETE, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 delete 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> deleteForEntity(String url, Type responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        return executeForEntity(url, HttpMethod.DELETE, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 delete 请求
     *
     * @param url
     * @param uriVariables
     */
    public void delete(String url, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        execute(url, HttpMethod.DELETE, httpEntity, uriVariables);
    }

    /**
     * 发送 delete 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T deleteForObject(String url, Type responseType, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        return executeForObject(url, HttpMethod.DELETE, httpEntity, responseType, uriVariables);
    }

    /**
     * 发送 delete 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> deleteForEntity(String url, Type responseType, Map<String, ?> uriVariables) {
        HttpEntity<Object> httpEntity = getHttpEntity(null);
        return executeForEntity(url, HttpMethod.DELETE, httpEntity, responseType, uriVariables);
    }
    //endregion

    //region execute

    /**
     * 根据 method 执行rest请求，返回对象
     *
     * @param url
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T executeForObject(String url, HttpMethod method, HttpEntity<?> requestEntity, Type responseType, Object... uriVariables) {
        URI uri = new UriTemplate(url).expand(uriVariables);
        HttpEntity<?> mergeRequestEntity = merge(requestEntity);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(mergeRequestEntity, responseType);
        ResponseExtractor<T> responseExtractor = restTemplate.httpMessageConverterExtractor(responseType);
        return doExecute(uri, method, requestCallback, responseExtractor);
    }

    /**
     * 根据 method 发送 rest 请求，返回带头信息的对象
     *
     * @param url
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> executeForEntity(String url, HttpMethod method, HttpEntity<?> requestEntity, Type responseType, Object... uriVariables) {
        URI uri = new UriTemplate(url).expand(uriVariables);
        HttpEntity<?> mergeRequestEntity = merge(requestEntity);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(mergeRequestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);

        return doExecute(uri, method, requestCallback, responseExtractor);
    }

    /**
     * 根据 method 执行 rest 请求，不返回数据
     *
     * @param url
     * @param method
     * @param requestEntity
     * @param uriVariables
     */
    public void execute(String url, HttpMethod method, HttpEntity<?> requestEntity, Object... uriVariables) {
        URI uri = new UriTemplate(url).expand(uriVariables);
        HttpEntity<?> mergeRequestEntity = merge(requestEntity);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(mergeRequestEntity);
        doExecute(uri, method, requestCallback, null);
    }

    /**
     * 根据 method 执行 rest 请求，返回对象
     *
     * @param url
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> T executeForObject(String url, HttpMethod method, HttpEntity<?> requestEntity, Type responseType, Map<String, ?> uriVariables) {
        URI uri = new UriTemplate(url).expand(uriVariables);
        HttpEntity<?> mergeRequestEntity = merge(requestEntity);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(mergeRequestEntity, responseType);
        ResponseExtractor<T> responseExtractor = restTemplate.httpMessageConverterExtractor(responseType);
        return doExecute(uri, method, requestCallback, responseExtractor);
    }

    /**
     * 根据 method 发送 rest 请求，返回 {@link org.springframework.http.ResponseEntity} 对象
     *
     * @param url
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @return
     */
    public <T> ResponseEntity<T> executeForEntity(String url, HttpMethod method, HttpEntity<?> requestEntity, Type responseType, Map<String, ?> uriVariables) {
        URI uri = new UriTemplate(url).expand(uriVariables);
        HttpEntity<?> mergeRequestEntity = merge(requestEntity);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(mergeRequestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);

        return doExecute(uri, method, requestCallback, responseExtractor);
    }

    /**
     * 根据 method 执行 rest 请求，不返回数据
     *
     * @param url
     * @param method
     * @param requestEntity
     * @param uriVariables
     */
    public void execute(String url, HttpMethod method, HttpEntity<?> requestEntity, Map<String, ?> uriVariables) {
        URI uri = new UriTemplate(url).expand(uriVariables);
        HttpEntity<?> mergeRequestEntity = merge(requestEntity);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(mergeRequestEntity);
        doExecute(uri, method, requestCallback, null);
    }
    //endregion

    protected HttpEntity<Object> getHttpEntity(Object requestBody) {
        return new HttpEntity<>(requestBody);
    }

    private HttpEntity<?> merge(HttpEntity<?> httpEntity) {
        HttpHeaders httpHeaders = mergerHeaders(httpEntity.getHeaders());
        return new HttpEntity<>(httpEntity.getBody(), httpHeaders);
    }

    /**
     * 基于此方法真正发送rest请求
     *
     * @param url
     * @param method
     * @param requestCallback
     * @param responseExtractor
     * @return
     */
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
        log.info("Http request begin. " + method + ": " + url);
        StopWatch sw = new StopWatch();
        sw.start();
        T result = restTemplate.execute(url, method, requestCallback, responseExtractor);
        sw.stop();
        log.info("Http request end. Total millis: " + sw.getTotalTimeMillis() + " " + method + ": " + url);

        return result;
    }

    /**
     * 发送 post 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #postForObject(String, Object, Class, Object...)}
     */
    @Deprecated
    public <T> T post(String url, Type responseType, Object... uriVariables) {
        return this.postForObject(url, null, responseType, uriVariables);
    }

    /**
     * 发送 post 请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #postForObject(String, Object, Class, Object...)}
     */
    @Deprecated
    public <T> T post(String url, Object requestBody, Type responseType, Object... uriVariables) {
        return this.postForObject(url, requestBody, responseType, uriVariables);
    }

    /**
     * 重载post方法
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #postForObject(String, Object, Class, Map)}
     */
    @Deprecated
    public <T> T post(String url, Type responseType, Map<String, ?> uriVariables) {
        return this.postForObject(url, null, responseType, uriVariables);
    }

    /**
     * 重载post方法，带body参数
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #postForObject(String, Object, Class, Map)}
     */
    @Deprecated
    public <T> T post(String url, Object requestBody, Type responseType, Map<String, ?> uriVariables) {
        return this.postForObject(url, requestBody, responseType, uriVariables);
    }

    /**
     * 没有参数的 post 请求方法
     *
     * @param url
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #postForObject(String, Object, Class, Object...)}
     */
    @Deprecated
    public <T> T post(String url, Type responseType) {
        return this.postForObject(url, null, responseType);
    }

    /**
     * 重载post方法，带body参数
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #postForObject(String, Object, Class, Object...)}
     */
    @Deprecated
    public <T> T post(String url, Object requestBody, Type responseType) {
        return this.postForObject(url, requestBody, responseType);
    }

    /**
     * get方法发送请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #getForObject(String, Class, Object...)}
     */
    @Deprecated
    public <T> T get(String url, Type responseType, Object... uriVariables) {
        return this.getForObject(url, responseType, uriVariables);
    }

    /**
     * get方法请求，带body参数
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #getForObject(String, Class, Object...)}
     */
    @Deprecated
    public <T> T get(String url, Object requestBody, Type responseType, Object... uriVariables) {
        return this.getForObject(url, responseType, uriVariables);
    }

    /**
     * 重载get方法，参数形式改为map
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #getForObject(String, Class, Map)}
     */
    @Deprecated
    public <T> T get(String url, Type responseType, Map<String, ?> uriVariables) {
        return this.getForObject(url, responseType, uriVariables);
    }

    /**
     * 重载get方法，带body参数
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #getForObject(String, Class, Map)}
     */
    @Deprecated
    public <T> T get(String url, Object requestBody, Type responseType, Map<String, ?> uriVariables) {
        return this.getForObject(url, responseType, uriVariables);
    }

    /**
     * 没有参数的get请求方法
     *
     * @param url
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #getForObject(String, Class, Object...)}
     */
    @Deprecated
    public <T> T get(String url, Type responseType) {
        return this.getForObject(url, responseType);
    }

    /**
     * 带body参数的get请求方法
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #getForObject(String, Class, Object...)}
     */
    @Deprecated
    public <T> T get(String url, Object requestBody, Type responseType) {
        return this.getForObject(url, responseType);
    }

    /**
     * 发送 delete 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #deleteForObject(String, Class, Object...)}
     */
    @Deprecated
    public <T> T delete(String url, Type responseType, Object... uriVariables) {
        return this.deleteForObject(url, responseType, uriVariables);
    }

    /**
     * 发送 delete 请求, 带body参数
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #deleteForObject(String, Class, Object...)}
     */
    @Deprecated
    public <T> T delete(String url, Object requestBody, Type responseType, Object... uriVariables) {
        return this.deleteForObject(url, responseType, uriVariables);
    }

    /**
     * 发送 delete 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #deleteForObject(String, Class, Map)}
     */
    @Deprecated
    public <T> T delete(String url, Type responseType, Map<String, ?> uriVariables) {
        return this.deleteForObject(url, responseType, uriVariables);
    }

    /**
     * 发送 delete 请求, 带body参数
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #deleteForObject(String, Class, Map)}
     */
    @Deprecated
    public <T> T delete(String url, Object requestBody, Type responseType, Map<String, ?> uriVariables) {
        return this.deleteForObject(url, responseType, uriVariables);
    }

    /**
     * 无参数的delete请求
     *
     * @param url
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #deleteForObject(String, Class, Object...)}
     */
    @Deprecated
    public <T> T delete(String url, Type responseType) {
        return this.deleteForObject(url, responseType);
    }

    /**
     * 带body参数的delete请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #deleteForObject(String, Class, Object...)}
     */
    @Deprecated
    public <T> T delete(String url, Object requestBody, Type responseType) {
        return this.deleteForObject(url, responseType);
    }

    /**
     * 发送 put 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #putForObject(String, Object, Class, Object...)}
     */
    @Deprecated
    public <T> T put(String url, Type responseType, Object... uriVariables) {
        return this.putForObject(url, null, responseType, uriVariables);
    }

    /**
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #putForObject(String, Object, Class, Object...)}
     */
    @Deprecated
    public <T> T put(String url, Object requestBody, Type responseType, Object... uriVariables) {
        return this.putForObject(url, requestBody, responseType, uriVariables);
    }

    /**
     * 发送 put 请求
     *
     * @param url
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #putForObject(String, Object, Class, Map)}
     */
    @Deprecated
    public <T> T put(String url, Type responseType, Map<String, ?> uriVariables) {
        return this.putForObject(url, null, responseType, uriVariables);
    }

    /**
     * 发送 put 请求, 带body参数
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #putForObject(String, Object, Class, Map)}
     */
    @Deprecated
    public <T> T put(String url, Object requestBody, Type responseType, Map<String, ?> uriVariables) {
        return this.putForObject(url, requestBody, responseType, uriVariables);
    }

    /**
     * 无参数的put的请求
     *
     * @param url
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #putForObject(String, Object, Class, Object...)}
     */
    @Deprecated
    public <T> T put(String url, Type responseType) {
        return this.putForObject(url, null, responseType);
    }

    /**
     * 带body参数的put请求
     *
     * @param url
     * @param requestBody
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #putForObject(String, Object, Class, Object...)}
     */
    @Deprecated
    public <T> T put(String url, Object requestBody, Type responseType) {
        return this.putForObject(url, requestBody, responseType);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Map)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Type responseType, Map<String, ?> uriVariables) {
        return this.executeForObject(url, method, null, responseType, uriVariables);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param responseType
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Object...)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Type responseType, Object... uriVariables) {
        return this.executeForObject(url, method, null, responseType, uriVariables);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Object...)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Type responseType) {
        return this.executeForObject(url, method, null, responseType);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param responseType
     * @param requestBody
     * @param requestHeaders
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Map)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Type responseType, Object requestBody, HttpHeaders requestHeaders, Map<String, ?> uriVariables) {
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, this.mergerHeaders(requestHeaders));
        return this.executeForObject(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param responseType
     * @param requestBody
     * @param requestHeaders
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Object...)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Type responseType, Object requestBody, HttpHeaders requestHeaders, Object... uriVariables) {
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, this.mergerHeaders(requestHeaders));
        return this.executeForObject(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param requestBody
     * @param responseType
     * @param requestHeaders
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Object...)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Object requestBody, Type responseType, HttpHeaders requestHeaders) {
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, this.mergerHeaders(requestHeaders));
        return this.executeForObject(url, method, requestEntity, responseType);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param responseType
     * @param requestBody
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Map)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Type responseType, Object requestBody, Map<String, ?> uriVariables) {
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, null);
        return this.executeForObject(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param responseType
     * @param requestBody
     * @param uriVariables
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Object...)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Type responseType, Object requestBody, Object... uriVariables) {
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, null);
        return this.executeForObject(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * 根据传入的方法名，发送相对应的请求
     *
     * @param url
     * @param method
     * @param requestBody
     * @param responseType
     * @return
     * @deprecated 请使用 {@link #executeForObject(String, HttpMethod, HttpEntity, Class, Object...)}
     */
    @Deprecated
    public <T> T exchange(String url, HttpMethod method, Object requestBody, Type responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, null);
        return this.executeForObject(url, method, requestEntity, responseType);
    }
}

