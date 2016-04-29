package com.nd.gaea.rest.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.nd.gaea.rest.filter.TokenAuthenticationProcessFilter;
import com.nd.gaea.rest.security.authentication.PreAuthenticatedAuthenticationExtractorManager;
import com.nd.gaea.rest.support.WafContext;

@Import(WafSecurityConfiguration.class)
public abstract class AbstractWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    public static final String ROLE_BIZ_SERVER = "role_biz_server";

    private PreAuthenticatedAuthenticationExtractorManager extractorManager;

    protected AbstractWebSecurityConfigurerAdapter() {
        super(true);
    }

    @Autowired
    public void setExtractorManager(PreAuthenticatedAuthenticationExtractorManager extractorManager) {
        this.extractorManager = extractorManager;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, List<AuthenticationProvider> wafProviders)
            throws Exception {

        for (AuthenticationProvider provider : wafProviders)
            auth.authenticationProvider(provider);
    }

    /**
     * 配置安全请求路径和角色的匹配，并且定制安全所需要的过滤器为5个
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .securityContext()
                .and()
                .addFilterAfter(tokenAuthenticationProcessFilter(),
                        SecurityContextPersistenceFilter.class).anonymous()
                .and();

        // 禁用的时候，不进行鉴权处理
        Boolean disabledSecurity = WafContext.isSecurityDisabled();

        if (!disabledSecurity) {
            this.onConfigure(http);
        }
    }

    /**
     * 扩展spring security的授权路径配置
     *
     * @param http
     * @throws Exception
     */
    protected abstract void onConfigure(HttpSecurity http) throws Exception;


    /**
     * 构建一个token的过滤器，此过滤器的主要目的是从请求头信息中获得token字符串信息
     *
     * @return
     * @throws Exception
     */
    protected TokenAuthenticationProcessFilter tokenAuthenticationProcessFilter() throws Exception {
        return new TokenAuthenticationProcessFilter(super.authenticationManager(), extractorManager);
    }

}
