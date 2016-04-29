/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.services.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nd.gaea.WafException;
import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import com.nd.gaea.rest.security.authentication.mac.PreAuthenticatedMacTokenAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.Assert;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class UserCenterMacTokenCacheService extends UserCenterMacTokenService {

    private static Cache<String, WafUcCheckToken> macTokenCache;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String WAF_MAC_TOKEN_CACHE_EXPIRE = "waf.macToken.cache.expire";
    public static final String WAF_MAC_TOKEN_CACHE_MAX_SIZE = "waf.macToken.cache.maxSize";

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.put(WAF_MAC_TOKEN_CACHE_MAX_SIZE, "10000");
        defaultProperties.put(WAF_MAC_TOKEN_CACHE_EXPIRE, "120");
    }
    
    public Cache<String, WafUcCheckToken> getMacTokenCache(){
    	return macTokenCache;
    }

    public UserCenterMacTokenCacheService(){
        this.initMacCache();
    }

    /**
     * 初始化mac cache
     */
    private void initMacCache() {
        macTokenCache = CacheBuilder
                .newBuilder().maximumSize(WafProperties.getPropertyForInteger(WAF_MAC_TOKEN_CACHE_MAX_SIZE))
                .expireAfterWrite(WafProperties.getPropertyForInteger(WAF_MAC_TOKEN_CACHE_EXPIRE), TimeUnit.MINUTES)
                .build();
    }

    private class CacheLoader implements Callable<WafUcCheckToken>{
        private final PreAuthenticatedMacTokenAuthentication authentication;
        public CacheLoader(PreAuthenticatedMacTokenAuthentication authentication){
            this.authentication = authentication;
        }

        @Override
        public WafUcCheckToken call() throws Exception {
            return invoke( authentication );
        }
    }

    /**
     * 调用父类获取WafUcCheckToken方法
     *
     * @param preAuthenticatedAuthentication 需要验证的请求信息
     * @return 验证通过返回用户的鉴权信息，验证失败返回null
     */
    private WafUcCheckToken invoke(Authentication preAuthenticatedAuthentication) {
        return super.verifyToken(preAuthenticatedAuthentication);
    }

    @Override
    public WafUcCheckToken verifyToken(Authentication preAuthenticatedAuthentication) {
        Assert.notNull(preAuthenticatedAuthentication, "preAuthenticatedAuthentication");
        PreAuthenticatedMacTokenAuthentication authentication = (PreAuthenticatedMacTokenAuthentication) preAuthenticatedAuthentication;
        WafUcCheckToken wafUcCheckToken = CacheUtil.get(macTokenCache, authentication.getId(), new CacheLoader(authentication));
        return this.checkMac(wafUcCheckToken, authentication);
    }

    public WafUcCheckToken checkMac(WafUcCheckToken wafUcCheckToken, PreAuthenticatedMacTokenAuthentication authRequest) {

        //throw exception like uc
    	Assert.notNull(wafUcCheckToken, "wafUcCheckToken");
    	Assert.notNull(authRequest, "authRequest");
    	
    	//判断access_token是否过期
    	if (wafUcCheckToken.isExpire()) {
    		throw new WafException("UC/AUTH_TOKEN_EXPIRED", "授权令牌已过期", HttpStatus.UNAUTHORIZED);
		}

        String sbRawMac = authRequest.getNonce() +
                "\n" +
                authRequest.getHttpMethod().toUpperCase() +
                "\n" +
                authRequest.getRequestUri() +
                "\n" +
                authRequest.getHost() +
                "\n";

        String newMac = encryptHMac256(sbRawMac, wafUcCheckToken.getMacKey());
        if (logger.isDebugEnabled()) {
            logger.debug("Mac key:{}, newMac:{}", authRequest.getMac(), newMac);
        }

        if (authRequest.getMac().equalsIgnoreCase(newMac)) {
            return wafUcCheckToken;
        }else {
        	throw new WafException("UC/MAC_SIGN_INVALID", "Mac签名错误", HttpStatus.UNAUTHORIZED);
		}
    }

    private static String encryptHMac256(String content, String key) {
    	Assert.notNull(content, "content");
    	Assert.notNull(key, "key");
        // 还原密钥
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        // 实例化Mac
        Mac mac;
        try {
            mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 执行消息摘要
        byte[] digest = mac.doFinal(content.getBytes());
        return new String(Base64.encode(digest));
    }
}
