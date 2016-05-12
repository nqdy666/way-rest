package com.nd.gaea.waf.client;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.util.Assert;

/**
 * 用于代理时进行授权及发送Accept
 * Created by vime on 2016/1/7.
 */
public class GaeaZuulFilter extends ZuulFilter {

    private IRequestProcessor processor;

    public GaeaZuulFilter(IRequestProcessor processor){
        Assert.notNull(processor, "processor can not be null.");
        this.processor = processor;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    // FilterRegistry.instance().put("waf", new GaeaZuulFilter());
    public Object run() {
        final RequestContext requestContext = RequestContext.getCurrentContext();

        processor.process(new IRequest() {
            @Override
            public void setHeader(String header, String value) {
                requestContext.addZuulRequestHeader(header,value);
            }
        });
        return null;
    }
}
