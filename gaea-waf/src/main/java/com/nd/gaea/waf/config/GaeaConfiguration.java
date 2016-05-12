package com.nd.gaea.waf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nd.gaea.rest.exceptions.WafErrorResolver;
import com.nd.gaea.util.WafJsonMapper;
import com.nd.gaea.waf.client.DeliverRequestProcessor;
import com.nd.gaea.waf.client.GaeaRequestInterceptor;
import com.nd.gaea.waf.client.GaeaZuulFilter;
import com.nd.gaea.waf.client.IRequestProcessor;
import com.nd.gaea.waf.exception.AuthenticationErrorResolver;
import com.netflix.zuul.filters.FilterRegistry;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.Assert;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Created by vime on 2016/2/24.
 */
@Configuration
public class GaeaConfiguration implements ApplicationListener<ContextRefreshedEvent>{
    @Bean
    @ConditionalOnMissingBean(value = AuthenticationErrorResolver.class)
    public WafErrorResolver authenticationErrorResolver() {
        return new AuthenticationErrorResolver();
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet ds = new DispatcherServlet();
        //找不到页面就抛异常
        ds.setThrowExceptionIfNoHandlerFound(true);
        return ds;
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer scanProperties() {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        p.setIgnoreUnresolvablePlaceholders(true);
        return p;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return WafJsonMapper.getMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public IRequestProcessor requestProcessor() {
        return new DeliverRequestProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor requestInterceptor() {
        return new GaeaRequestInterceptor();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // init for zuul filter
        IRequestProcessor requestProcessor = event.getApplicationContext().getBean("requestProcessor", IRequestProcessor.class);
        GaeaRequestInterceptor requestInterceptor = (GaeaRequestInterceptor) event.getApplicationContext().getBean("requestInterceptor", RequestInterceptor.class);
        Assert.notNull(requestProcessor, "requestProcessor can not be null.");
        FilterRegistry.instance().put("gaea", new GaeaZuulFilter(requestProcessor));
        requestInterceptor.setProcessor(requestProcessor);
    }
}
