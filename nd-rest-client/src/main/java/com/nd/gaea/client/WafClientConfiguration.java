package com.nd.gaea.client;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.SpanCollector;
import com.nd.gaea.WafProperties;
import com.nd.gaea.client.auth.WafBearerTokenProvider;
import com.nd.gaea.client.auth.WafBearerTokenService;
import com.nd.gaea.client.auth.WafBearerTokenServiceImpl;
import com.nd.gaea.client.http.BearerAuthorizationProvider;
import com.nd.gaea.client.http.SimpleBearerAuthorizationProvider;
import com.nd.gaea.client.http.WafHttpClient;
import com.nd.gaea.client.http.WafSecurityHttpClient;
import com.nd.gaea.client.support.WafContext;
import com.nd.gaea.client.support.ZipkinSpanCollectorWarpper;
import com.nd.sdp.etconfig.apply.TraceApply;
import com.nd.sdp.trace.TraceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author vime
 * @since 0.9.6
 */
@Configuration
@ComponentScan(basePackages = {"com.github.kristofa.brave"})
public class WafClientConfiguration {
    /**
     * 跟踪信息的采样率，默认值为1。值为100表示 1/100的采样率。
     *
     * @since 0.9.6
     */
    public static final String WAF_ETCD_HOST = "waf.etcd.host";
    public static final String WAF_ETCD_HOST_DEFAULT = "http://etcd.product.sdp.nd:2379";
    public static final String WAF_APP_NAME = "waf.app.name";
    public static final String WAF_APP_NAME_DEFAULT = "waf";

    static {
        //保证在创建http client之前已经初始化好各tracer的实现
        TraceConfig.instance();
    }

    public SpanCollector spanCollector() {
        boolean traceEnabled = WafContext.isTraceEnabled();

        SpanCollector spanCollector;
        if (traceEnabled) {
            spanCollector = new ZipkinSpanCollectorWarpper();
        }else {
            spanCollector = new WafLoggingSpanCollectorImpl();
        }

        return spanCollector;
    }

    @Bean
    public Brave brave(){
        TraceApply.init(WafProperties.getProperty(WAF_ETCD_HOST, WAF_ETCD_HOST_DEFAULT),
                WafProperties.getProperty(WAF_APP_NAME, WAF_APP_NAME_DEFAULT));

        return  TraceConfig.instance().setSpanCollector(spanCollector()).build();
    }

    @Bean
    public ApplicationContextUtil applicationContextUtil() {
        return new ApplicationContextUtil();
    }

    @Bean
    public WafBearerTokenProvider wafBearerTokenProvider() {
        return new WafBearerTokenProvider();
    }

    @Bean
    public WafBearerTokenService WafBearerTokenService() {
        return new WafBearerTokenServiceImpl();
    }

    @Bean
    public BearerAuthorizationProvider bearerAuthorizationProvider() {
        return new SimpleBearerAuthorizationProvider();
    }

    @Bean
    public WafHttpClient wafHttpClient() {
        return new WafHttpClient();
    }

    @Bean
    public WafSecurityHttpClient wafSecurityHttpClient() {
        return new WafSecurityHttpClient();
    }
}
