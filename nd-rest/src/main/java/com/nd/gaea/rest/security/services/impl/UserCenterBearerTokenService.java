package com.nd.gaea.rest.security.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.http.WafHttpClient;
import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import com.nd.gaea.rest.security.authentication.bearer.PreAuthenticatedBearerTokenAuthentication;
import com.nd.gaea.rest.security.services.TokenService;
import com.nd.gaea.rest.support.WafContext;
import com.nd.gaea.util.UrlUtil;

/**
 * @author vime
 * @since 0.9.5
 */
public class UserCenterBearerTokenService implements TokenService {
    public static final String UC_FRAGMENT_BEARER_TOKEN_VALID = "waf.uc.bearerToken.valid";

    @Autowired
    @Qualifier("wafHttpClient")
    private WafHttpClient httpClient;

    private String getBearerTokenValidUrl() {
        return UrlUtil.combine(
                WafProperties.getProperty(WafContext.WAF_UC_URI),
                WafProperties.getProperty(UC_FRAGMENT_BEARER_TOKEN_VALID, "bearer_tokens/{bearer_token}/actions/valid")
        );
    }

    @Autowired
    public WafHttpClient setWafHttpClient(WafHttpClient wafHttpClient)
    {
        return this.httpClient = wafHttpClient;
    }

    @Override
    public WafUcCheckToken verifyToken(
            Authentication preAuthenticatedAuthentication) {
        Assert.notNull(preAuthenticatedAuthentication, "preAuthenticatedAuthentication");

        PreAuthenticatedBearerTokenAuthentication bearerTokenAuthentication = (PreAuthenticatedBearerTokenAuthentication) preAuthenticatedAuthentication;
        String bearerTokenValidUrl = this.getBearerTokenValidUrl();
        return httpClient.getForObject(bearerTokenValidUrl, WafUcCheckToken.class, bearerTokenAuthentication.getBearerToken());
    }

}
