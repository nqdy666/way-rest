package com.nd.gaea.waf.client;

import com.nd.gaea.client.ApplicationContextUtil;
import com.nd.gaea.client.auth.WafBearerTokenProvider;
import com.nd.gaea.client.http.WafHttpClient;
import com.nd.gaea.client.http.WafRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by vime on 2016/2/19.
 */
public class GaeaHttpClient extends WafHttpClient {
    public GaeaHttpClient(int retryCount) {
        super(retryCount);
    }

    public GaeaHttpClient(int retryCount, Collection<Class<? extends IOException>> clazzes) {
        super(retryCount, clazzes);
    }

    public GaeaHttpClient(int connectTimeout, int socketTimeout) {
        super(connectTimeout, socketTimeout);
    }

    public GaeaHttpClient(int connectTimeout, int socketTimeout, int retryCount) {
        super(connectTimeout, socketTimeout, retryCount);
    }

    public GaeaHttpClient(int connectTimeout, int socketTimeout, int retryCount, Collection<Class<? extends IOException>> clazzes) {
        super(connectTimeout, socketTimeout, retryCount, clazzes);
    }

    public GaeaHttpClient(WafRestTemplate restTemplate) {
        super(restTemplate);
    }

    private IRequestProcessor requestProcessor;

    public void setRequestProcessor(IRequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public IRequestProcessor getRequestProcessor() {
        if(requestProcessor == null)
        {
            ApplicationContext context = ApplicationContextUtil.getApplicationContext();
            Assert.notNull(context, "Application context cannot be null.");
            requestProcessor = context.getBean("requestProcessor", IRequestProcessor.class);
        }
        return requestProcessor;
    }

    @Override
    protected HttpHeaders mergerHeaders(final HttpHeaders headers) {
        final HttpHeaders innerHeaders = super.mergerHeaders(headers);
        IRequestProcessor processor = getRequestProcessor();
        processor.process(new IRequest() {
            @Override
            public void setHeader(String header, String value) {
                innerHeaders.add(header,value);
            }
        });
        return innerHeaders;

    }
}
