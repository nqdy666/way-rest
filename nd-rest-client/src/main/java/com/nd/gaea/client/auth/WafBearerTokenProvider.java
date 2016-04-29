package com.nd.gaea.client.auth;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.entity.WafBearerToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * token服务提供类
 *
 * @author vime
 * @since 0.9.5
 */
public class WafBearerTokenProvider {
    public static final String WAF_CLIENT_MIN_REFRESH_SPAN = "waf.client.minRefreshSpan";
    private static final String WAF_CLIENT_MIN_REFRESH_SPAN_VALUE = "300000";

    private WafBearerToken bearerToken;
    private int minRefreshSpan;
    private WafBearerTokenService wafBearerTokenService;


    public WafBearerTokenProvider() {
        minRefreshSpan = WafProperties.getPropertyForInteger(WAF_CLIENT_MIN_REFRESH_SPAN, WAF_CLIENT_MIN_REFRESH_SPAN_VALUE);
    }

    @Autowired
    public void setWafBearerTokenService(WafBearerTokenService wafBearerTokenService)
    {
        this.wafBearerTokenService = wafBearerTokenService;
    }


    public WafBearerToken getToken() {
    	if (bearerToken == null) {
            synchronized (this) {
            	if (bearerToken==null) {
            		bearerToken = wafBearerTokenService.getBearerToken();
				}
            }
        }
        Assert.notNull(bearerToken, "bearerToken cannot be null.");
        if (shouldRefreshToken(bearerToken)) {
            synchronized (this) {
                bearerToken = wafBearerTokenService.refreshBearerToken(bearerToken);
            }
            Assert.notNull(bearerToken, "bearerToken cannot be null.");
        }
        return bearerToken;
    }

    public void setToken(WafBearerToken token) {
        Assert.notNull(token, "token cannot be null.");
        synchronized (this) {
            this.bearerToken = token;
        }
    }
    
    public void resetDelayToken(){
    	 synchronized (this) {
             this.bearerToken = null;
         }
    }

    public boolean isAuthorized() {
        return bearerToken != null && !bearerToken.isExpire();
    }

    protected boolean shouldRefreshToken(WafBearerToken bearerToken) {
        Assert.notNull(bearerToken, "bearerToken cannot be null.");
        Date now = new Date();
        return bearerToken.getExpiresAt().getTime() - now.getTime() < minRefreshSpan;
    }
}
