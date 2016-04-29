package com.nd.gaea.rest.config;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.WafClientConfiguration;
import com.nd.gaea.rest.exceptions.DefaultWafRestErrorResolver;
import com.nd.gaea.rest.exceptions.FriendlyWafRestErrorResolver;
import com.nd.gaea.rest.exceptions.WafErrorResolver;
import com.nd.gaea.rest.exceptions.WafRestErrorResolver;
import com.nd.gaea.rest.exceptions.rest.RestErrorMappings;
import com.nd.gaea.rest.filter.ExceptionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author vime
 * @since 0.9.6
 */
@Configuration
@ComponentScan(basePackages = {"com.nd.gaea.rest.exceptions.friendly"})
@Import(value = {WafClientConfiguration.class})
public class WafConfiguration {

    public final static String WAF_EXCEPTION_FRIENDLY_DISABLED = "waf.exception.friendly.disabled";

    /**
     * 对异常进行拦截 ServletFilter（为了支持 Spring bean）
     *
     * @return
     */
    @Bean
    public ExceptionFilter exceptionFilter() {
        return new ExceptionFilter();
    }

    /**
     * 具体执行异常处理
     *
     * @return
     */
    @Bean
    public WafErrorResolver wafErrorResolver() {
        WafRestErrorResolver resolver;
        if (Boolean.parseBoolean(WafProperties.getProperty(WAF_EXCEPTION_FRIENDLY_DISABLED)))
            resolver = new WafRestErrorResolver();
        else
            resolver = new FriendlyWafRestErrorResolver();

        for (RestErrorMappings mapping : RestErrorMappings.values()) {
            resolver.addHandler(mapping.getThrowableClass(), mapping.getHandler());
        }

        return resolver;
    }

    @Bean
    public WafErrorResolver defaultWafRestErrorResolver() {
        return new DefaultWafRestErrorResolver();
    }

    @Bean
    public WafPropertiesListener wafPropertiesListener() {
        return new WafPropertiesListener();
    }
}

