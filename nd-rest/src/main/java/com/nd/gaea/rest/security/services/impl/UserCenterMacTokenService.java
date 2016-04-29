/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.http.WafHttpClient;
import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import com.nd.gaea.rest.security.authentication.mac.PreAuthenticatedMacTokenAuthentication;
import com.nd.gaea.rest.security.services.TokenService;
import com.nd.gaea.rest.support.WafContext;
import com.nd.gaea.util.UrlUtil;

/**
 * Token的验证服务。
 *
 * @author johnny
 */
public class UserCenterMacTokenService implements TokenService {

    public static final String UC_FRAGMENT_MAC_TOKEN_VALID = "waf.uc.macToken.valid";
    
    @Autowired
    @Qualifier("wafHttpClient")
    private WafHttpClient wafHttpClient;


    private String getTokenValidUrl() {
        return UrlUtil.combine(
                WafProperties.getProperty(WafContext.WAF_UC_URI),
                WafProperties.getProperty(UC_FRAGMENT_MAC_TOKEN_VALID, "tokens/{access_token}/actions/valid")
        );
    }

    @Autowired
    public WafHttpClient setWafHttpClient(WafHttpClient wafHttpClient)
    {
        return this.wafHttpClient = wafHttpClient;
    }

    /**
     * 根据传入进来的token头信息，构建请求，去验证token的正确性 其中key为UC验证所需要的请求信息的json字符串
     * 如果返回的token不为空，则证明token验证成功。 如果返回时空，说明验证失败，并且有异常错误信息存在。
     * 错误信息存放在全局变量Exception中。
     */
    @Override
    public WafUcCheckToken verifyToken(Authentication preAuthenticatedAuthentication)  {
        Assert.notNull(preAuthenticatedAuthentication, "preAuthenticatedAuthentication");

        PreAuthenticatedMacTokenAuthentication authentication = (PreAuthenticatedMacTokenAuthentication) preAuthenticatedAuthentication;
        String tokenValidUrl = this.getTokenValidUrl();
        return wafHttpClient.postForObject(tokenValidUrl, preAuthenticatedAuthentication, WafUcCheckToken.class, authentication.getId());
    }
}
