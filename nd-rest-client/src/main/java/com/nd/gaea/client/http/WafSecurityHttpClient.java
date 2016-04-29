package com.nd.gaea.client.http;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import com.nd.gaea.client.ApplicationContextUtil;
import com.nd.gaea.client.WafResourceAccessException;
import com.nd.gaea.client.auth.WafBearerTokenProvider;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import com.nd.gaea.client.support.WafClientContextHolder;

/**
 * 封装bearer token头验证http请求工具类
 *
 * @author vime
 * @since 0.9.5
 */
public class WafSecurityHttpClient extends WafHttpClient {
	
    private BearerAuthorizationProvider bearerAuthorizationProvider;
    
    public WafSecurityHttpClient() {
        super(WAF_CLIENT_CONNECT_TIMEOUT_INT_VALUE, WAF_CLIENT_SOCKET_TIMEOUT_INT_VALUE);
    }
    
    public WafSecurityHttpClient(int retryCount) {
    	super(WAF_CLIENT_CONNECT_TIMEOUT_INT_VALUE, WAF_CLIENT_SOCKET_TIMEOUT_INT_VALUE, retryCount);
    }

    public WafSecurityHttpClient(int retryCount, Collection<Class<? extends IOException>> clazzes) {
        super(WAF_CLIENT_CONNECT_TIMEOUT_INT_VALUE, WAF_CLIENT_SOCKET_TIMEOUT_INT_VALUE, retryCount, clazzes);
    }
    /**
     * @param connectTimeout 连接超时时间（毫秒），默认 5000 ms
     * @param socketTimeout  socket读写数据超时时间（毫秒），默认 10000 ms
     */
    public WafSecurityHttpClient(int connectTimeout, int socketTimeout) {
    	super(connectTimeout, socketTimeout);
    }

    public WafSecurityHttpClient(WafRestTemplate restTemplate) {
        Assert.notNull(restTemplate);
        super.restTemplate = restTemplate;
    }

    @Autowired(required = false)
    public void setBearerAuthorizationProvider(BearerAuthorizationProvider bearerAuthorizationProvider) {
        this.bearerAuthorizationProvider = bearerAuthorizationProvider;
    }
    
	public WafBearerTokenProvider getWafBearerTokenProvider() {
		return WafClientContextHolder.getProvider();
	}

    @Override
    protected HttpHeaders mergerHeaders(HttpHeaders headers) {
        headers = super.mergerHeaders(headers);
        HttpHeaders tempHeaders = new HttpHeaders();
        if (headers != null) {
            tempHeaders.putAll(headers);
        }

        if(bearerAuthorizationProvider == null)
        {
            // find in bean
            ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
            if(applicationContext!= null)
                bearerAuthorizationProvider = applicationContext.getBean(BearerAuthorizationProvider.class);
        }
        Assert.notNull(bearerAuthorizationProvider);
        
        if (bearerAuthorizationProvider!=null) {
        	tempHeaders.add(org.apache.http.HttpHeaders.AUTHORIZATION, bearerAuthorizationProvider.getAuthorization());
        	if (!StringUtils.isEmpty(bearerAuthorizationProvider.getUserid())) {
        		tempHeaders.add(BearerAuthorizationProvider.USERID, bearerAuthorizationProvider.getUserid());
			}
		}

        return tempHeaders;
    }
	
		@Override
	protected <T> T doExecute(URI url, HttpMethod method,
			RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) {
		try {
			return super.doExecute(url, method, requestCallback, responseExtractor);
		} catch (WafResourceAccessException e) {
			log.error(e.getMessage(), e);
			ResponseEntity<ResponseErrorMessage> responseEntity = e.getRemoteResponseEntity();
			HttpStatus statusCode = responseEntity.getStatusCode();
			if (statusCode.value()==401) {
				getWafBearerTokenProvider().resetDelayToken();
			}
			throw e;
		}
	}
}
