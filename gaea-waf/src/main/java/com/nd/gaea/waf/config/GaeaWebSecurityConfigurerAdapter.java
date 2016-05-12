package com.nd.gaea.waf.config;

import com.nd.gaea.rest.config.WafWebSecurityConfigurerAdapter;
import com.nd.gaea.rest.filter.TokenAuthenticationProcessFilter;
import com.nd.gaea.rest.security.authentication.PreAuthenticatedAuthenticationExtractorManager;
import com.nd.gaea.rest.security.services.RealmService;
import com.nd.gaea.rest.security.services.TokenService;
import com.nd.gaea.waf.security.gaea.GaeaTokenAuthenticationProcessFilter;
import com.nd.gaea.waf.security.gaea.AuthenticationProviderManager;
import com.nd.gaea.waf.security.gaea.GaeaTokenParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 *
 */
public class GaeaWebSecurityConfigurerAdapter extends WafWebSecurityConfigurerAdapter {
    private PreAuthenticatedAuthenticationExtractorManager extractorManager;
    private AuthenticationProviderManager authenticationProviderManager;
    private TokenService bearerTokenService;
    private RealmService realmService;

    @Autowired
    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    @Autowired
    public void setBearerTokenService(@Qualifier("bearer_token_service") TokenService bearerTokenService) {
        this.bearerTokenService = bearerTokenService;
    }

    @Autowired
    public void setExtractorManager(PreAuthenticatedAuthenticationExtractorManager extractorManager) {
        this.extractorManager = extractorManager;
    }

    @Autowired
    public void setAuthenticationProviderManager(AuthenticationProviderManager authenticationProviderManager) {
        this.authenticationProviderManager = authenticationProviderManager;
    }

    @Override
    protected TokenAuthenticationProcessFilter tokenAuthenticationProcessFilter() throws Exception {
        return new GaeaTokenAuthenticationProcessFilter(super.authenticationManager(), extractorManager, authenticationProviderManager, new GaeaTokenParser(bearerTokenService), realmService);
    }

    @Override
    protected void onConfigure(HttpSecurity http) throws Exception {
        super.onConfigure(http);
    }
}
