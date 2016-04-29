package com.nd.gaea.rest.filter;

import com.github.kristofa.brave.http.BraveHttpHeaders;
import com.google.common.base.Optional;
import com.nd.gaea.rest.support.WafContext;
import com.nd.sdp.trace.http.HttpServerTracer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.google.common.base.Optional.fromNullable;

/**
 * czb on 2015/11/3.
 */
public class ServerTraceInterceptor extends HandlerInterceptorAdapter {
    private HttpServerTracer httpServerTracer = new HttpServerTracer();
    private boolean traceEnabled;

    private boolean isTraceEnabled(HttpServletRequest request) {
        traceEnabled = WafContext.isTraceEnabled();
        if (!traceEnabled) {
            final Optional<String> sampled = fromNullable(request.getHeader(BraveHttpHeaders.Sampled.getName()));
            traceEnabled = Boolean.valueOf(sampled.or(Boolean.FALSE.toString()));
        }
        return traceEnabled;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if ( isTraceEnabled(request) ) {
            httpServerTracer.begin(request);
        }
        else {
            httpServerTracer.stopTrace();
        }

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception{
        if ( traceEnabled ){
            httpServerTracer.end(response);
        }
    }
}
