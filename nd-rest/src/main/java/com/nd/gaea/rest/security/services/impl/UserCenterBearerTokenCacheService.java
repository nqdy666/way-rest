
package com.nd.gaea.rest.security.services.impl;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nd.gaea.WafException;
import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import com.nd.gaea.rest.security.authentication.bearer.PreAuthenticatedBearerTokenAuthentication;

/**
 * bearer token 缓存验证类
 *
 * @author 110825
 * @since 0.9.5
 */
public class UserCenterBearerTokenCacheService extends UserCenterBearerTokenService {

    private static LoadingCache<PreAuthenticatedBearerTokenAuthentication, WafUcCheckToken> bearerTokenCache;

    public static final String WAF_BEARER_TOKEN_CACHE_EXPIRE = "waf.bearerToken.cache.expire";
    public static final String WAF_BEARER_TOKEN_CACHE_MAX_SIZE = "waf.bearerToken.cache.maxSize";

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.put(WAF_BEARER_TOKEN_CACHE_EXPIRE, "1440");
        defaultProperties.put(WAF_BEARER_TOKEN_CACHE_MAX_SIZE, "1000");
    }
    
    public LoadingCache<PreAuthenticatedBearerTokenAuthentication, WafUcCheckToken> getBearerTokenCache(){
    	this.initBearerTokenCache();
    	return bearerTokenCache;
    }

    /**
     * 初始化bearerToken cache
     */
    private void initBearerTokenCache() {
        if (bearerTokenCache == null) {
            synchronized (this) {
                if (bearerTokenCache == null) {
                    bearerTokenCache = CacheBuilder.newBuilder()
                            .maximumSize(WafProperties.getPropertyForInteger(WAF_BEARER_TOKEN_CACHE_MAX_SIZE))
                            .expireAfterWrite(WafProperties.getPropertyForInteger(WAF_BEARER_TOKEN_CACHE_EXPIRE), TimeUnit.MINUTES)
                            .build(new CacheLoader<PreAuthenticatedBearerTokenAuthentication, WafUcCheckToken>() {
                                @Override
                                public WafUcCheckToken load(PreAuthenticatedBearerTokenAuthentication key) throws Exception {
                                    return invoke(key);
                                }
                            });
                }
            }
        }
    }

    /**
     * 调用父类获取WafUcCheckToken方法
     */
    private WafUcCheckToken invoke(PreAuthenticatedBearerTokenAuthentication preAuthenticatedAuthentication) {
        return super.verifyToken(preAuthenticatedAuthentication);
    }

    @Override
    public WafUcCheckToken verifyToken(Authentication preAuthenticatedAuthentication) {
        Assert.notNull(preAuthenticatedAuthentication, "preAuthenticatedAuthentication");

        PreAuthenticatedBearerTokenAuthentication bearerTokenAuthentication = (PreAuthenticatedBearerTokenAuthentication) preAuthenticatedAuthentication;
        //初始化缓存实现
        this.initBearerTokenCache();
        WafUcCheckToken wafUcCheckToken = CacheUtil.get(bearerTokenCache, bearerTokenAuthentication);

        Assert.notNull(wafUcCheckToken, "WafUcCheckToken");
        if (wafUcCheckToken.isExpire()) {
        	throw new WafException("UC/AUTH_TOKEN_EXPIRED", "授权令牌已过期", HttpStatus.UNAUTHORIZED);
		}
        //throw exception like uc
        if (wafUcCheckToken.getBearerToken().equalsIgnoreCase(bearerTokenAuthentication.getBearerToken())) {
            return wafUcCheckToken;
        }else {
        	throw new WafException("UC/AUTH_INVALID_TOKEN", "无效的授权令牌", HttpStatus.UNAUTHORIZED);
		}
    }
}
