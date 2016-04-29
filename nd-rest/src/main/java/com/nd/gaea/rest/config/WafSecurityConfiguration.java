package com.nd.gaea.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

import com.nd.gaea.rest.security.authentication.PreAuthenticatedAuthenticationExtractorManager;
import com.nd.gaea.rest.security.services.RealmService;
import com.nd.gaea.rest.security.services.TokenService;
import com.nd.gaea.rest.security.services.WafUserDetailsService;
import com.nd.gaea.rest.security.services.impl.MacTokenService;
import com.nd.gaea.rest.security.services.impl.RealmServiceImpl;
import com.nd.gaea.rest.security.services.impl.UserCenterBearerTokenCacheService;
import com.nd.gaea.rest.security.services.impl.UserCenterMacTokenCacheNonceService;
import com.nd.gaea.rest.security.services.impl.UserCenterUserDetailsCacheService;
import com.nd.gaea.rest.security.services.impl.UserCenterUserDetailsProxyService;
import com.nd.gaea.rest.security.services.visitor.OrganizationCacheService;
import com.nd.gaea.rest.security.services.visitor.OrganizationService;
import com.nd.gaea.rest.security.services.visitor.OrganizationVisitorsAdapter;
import com.nd.gaea.rest.security.services.visitor.VisitorsAdapter;

/**
 * @author vime
 * @since 0.9.6
 */
@Configuration
@Import(WafConfiguration.class)
@ComponentScan(basePackages = {"com.nd.gaea.rest.security.*"})
@EnableWebMvcSecurity
public class WafSecurityConfiguration {
    /**
     * 提供从请求获取认证对象的Extractor管理器
     *
     * @return
     */
    @Bean
    public PreAuthenticatedAuthenticationExtractorManager preAuthenticatedAuthenticationExtractorManager() {
        return new PreAuthenticatedAuthenticationExtractorManager();
    }

    /**
     * 用户信息的服务
     *
     * @return
     */
    @Bean
    public WafUserDetailsService wafUserDetailsService() {
        return new UserCenterUserDetailsProxyService();
    }

    @Bean(name={"origin_waf_user_details_service"})
    public WafUserDetailsService originWafUserDetailsService() {
        return new UserCenterUserDetailsCacheService();
    }
    /**
     * 获得实现 Mac token 逻辑的服务
     *
     * @return
     */
    @Bean(name = {"mac_token_service"})
    public TokenService getMacTokenService() {
        return new UserCenterMacTokenCacheNonceService();
    }

    /**
     * 获得实现 Bearer token 逻辑的服务
     *
     * @return
     */
    @Bean(name = {"bearer_token_service"})
    public TokenService getBearerTokenService() {
        return new UserCenterBearerTokenCacheService();
    }

    /**
     * 提供当前业务的 realm 信息
     *
     * @return
     */
    @Bean
    public RealmService realmService() {
        return new RealmServiceImpl();
    }
    
	@Bean
	public OrganizationService organizationService() {
		return new OrganizationCacheService();
	}
	
	@Bean
	public MacTokenService macTokenService(){
		return new MacTokenService();
	}
	
	@Bean
    public VisitorsAdapter visitorsAdapter() {
        return new OrganizationVisitorsAdapter();
    }
}
