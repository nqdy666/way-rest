package com.nd.gaea.waf.config;

import com.nd.gaea.I18NProvider;
import com.nd.gaea.rest.AbstractWafWebApplicationInitializer;
import com.nd.gaea.rest.filter.WafCorsFilter;
import com.nd.gaea.waf.i18n.GaeaI18NProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Created by vime on 2016/1/7.
 */
@Configuration
@EnableZuulProxy
@EnableFeignClients("com")
@Import({GaeaConfiguration.class, GaeaSecurityConfiguration.class})
public class WafAutoConfiguration {
    @Configuration
    @EnableWebMvc
    public static class GaeaWebMvcAutoConfigurationAdapter extends GaeaWebMvcConfigurerAdapter {
    }

    @Configuration
    @EnableWebSecurity
    @ConfigurationProperties(locations = "classpath:waf.properties", prefix = "waf")
    public static class GaeaWebSecurityAutoConfigurerAdapter extends GaeaWebSecurityConfigurerAdapter {

        public GaeaWebSecurityAutoConfigurerAdapter() {
        }

        private String unAuthenticated;

        public String getUnAuthenticated() {
            return unAuthenticated;
        }

        public void setUnAuthenticated(String unAuthenticated) {
            this.unAuthenticated = unAuthenticated;
        }

        @Override
        protected void onConfigure(HttpSecurity http) throws Exception {
            if (StringUtils.isEmpty(unAuthenticated)) {
                http.authorizeRequests().anyRequest().authenticated();
            } else {
                String[] unPath = unAuthenticated.split(",");
                http.authorizeRequests().antMatchers(unPath).permitAll().anyRequest().authenticated();
            }
        }
    }

    @Configuration
    @Order(1)
    public static class GaeaWebApplicationInitializer extends AbstractWafWebApplicationInitializer {
        @Override
        protected Class<?>[] getServletConfigClasses() {
            return new Class<?>[0];
        }

        @Override
        public void onStartup(ServletContext servletContext)
                throws ServletException {

            this.registerFilters(servletContext);
            I18NProvider.setProvider(new GaeaI18NProvider());
        }

        @Override
        protected void registerFilters(ServletContext servletContext) {
            addFilter(servletContext, "exceptionFilter", new DelegatingFilterProxy("exceptionFilter"));
            initCharacterEncodingFilter(servletContext);
            addFilter(servletContext, "wafCorsFilter", new WafCorsFilter());
//            addFilter(servletContext, "wafHttpMethodOverrideFilter", new WafHttpMethodOverrideFilter());
        }
    }


}
