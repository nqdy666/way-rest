package com.nd.gaea.waf.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 *
 * Created by vime on 2016/1/7.
 */
public class GaeaRequestInterceptor implements RequestInterceptor {
    private IRequestProcessor processor;

    public IRequestProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(IRequestProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void apply(final RequestTemplate requestTemplate) {
        processor.process(new IRequest() {
            @Override
            public void setHeader(String header, String value) {
                requestTemplate.header(header,value);
            }
        });
    }
}
