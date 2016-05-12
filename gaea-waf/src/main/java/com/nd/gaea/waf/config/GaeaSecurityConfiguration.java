package com.nd.gaea.waf.config;


import com.nd.gaea.waf.security.service.IMacTokenProviderService;
import com.nd.gaea.waf.security.service.UserCenterMacTokenProviderService;
import com.nd.gaea.waf.security.gaea.AuthenticationProviderManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = "com.nd.gaea.waf.security")
public class GaeaSecurityConfiguration {
    @Bean
    public IMacTokenProviderService macTokenProviderService()
    {
        return new UserCenterMacTokenProviderService();
    }

    @Bean
    public AuthenticationProviderManager authenticationProviderManager(){
        return new AuthenticationProviderManager();
    }
}


