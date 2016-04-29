package com.nd.gaea.rest.security.services.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nd.gaea.WafException;
import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import com.nd.gaea.rest.security.authentication.mac.PreAuthenticatedMacTokenAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 基于macToken缓存服务类，扩展nonce校验机制
 * @author 110825
 * @since 0.9.5
 */
public class UserCenterMacTokenCacheNonceService extends
		UserCenterMacTokenCacheService {
	
	private static LoadingCache<String, Object> nonceCache;
	private final long nonceCacheExpire;
	
	public static final String WAF_NONCE_CACHE_EXPIRE = "waf.nonce.cache.expire";

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.put(WAF_NONCE_CACHE_EXPIRE, "5");
    }
    
    /**
     * 获取nonceCache，用于查询nonce串是否存在
     * @return
     */
    public LoadingCache<String, Object> getNonceCache(){
    	return nonceCache;
    }

	public UserCenterMacTokenCacheNonceService(){
		this.nonceCacheExpire = WafProperties.getPropertyForInteger(WAF_NONCE_CACHE_EXPIRE)*60*1000;
		this.initNonceCache();
	}
	
    private void initNonceCache() {
		nonceCache = CacheBuilder
				.newBuilder()
				.expireAfterWrite(WafProperties.getPropertyForInteger(WAF_NONCE_CACHE_EXPIRE), TimeUnit.MINUTES)
				.build(new CacheLoader<String, Object>() {
					@Override
					public Object load(String key) throws Exception {
						return key;
					}
				});
    }
	
	@Override
	public WafUcCheckToken verifyToken(
			Authentication preAuthenticatedAuthentication) {
		Assert.notNull(preAuthenticatedAuthentication, "preAuthenticatedAuthentication");
        PreAuthenticatedMacTokenAuthentication authentication = (PreAuthenticatedMacTokenAuthentication) preAuthenticatedAuthentication;

        WafUcCheckToken wafUcCheckToken = super.verifyToken(preAuthenticatedAuthentication);
		//校验nonce串的有效性，防止回放攻击
        this.checkNonce(authentication.getNonce());
        return wafUcCheckToken;
	}
	
	/**
	 * nonce有效性校验，防止回放攻击
	 * @param nonce
	 */
	public void checkNonce(String nonce) {
		if(StringUtils.isEmpty(nonce)){
			throw new WafException("UC/NONCE_INVALID", "Nonce串不能为空", HttpStatus.UNAUTHORIZED);
		}
		
		String[] strs = nonce.split(":");
		if(strs.length!=2 || StringUtils.isEmpty(strs[0]) || !Pattern.compile("[0-9]*").matcher(strs[0]).matches()){
			throw new WafException("UC/NONCE_INVALID", "Nonce串格式不正确", HttpStatus.UNAUTHORIZED);
		}

		long diff = new Date().getTime() - Long.parseLong(strs[0]);
		if (diff>nonceCacheExpire || diff<-nonceCacheExpire) {
			throw new WafException("UC/AUTH_INVALID_TIMESTAMP", "Nonce串无效,时间戳与系统的差异大于5分钟", HttpStatus.UNAUTHORIZED);
		}
		
		//验证nonce串缓存中是否存在
		String cacheNonce = (String)nonceCache.getIfPresent(strs[1]);
		if (!StringUtils.isEmpty(cacheNonce)) {
			//说明nonce串使用过了，抛出异常
			throw new WafException("UC/NONCE_INVALID", "Nonce串不能重复使用", HttpStatus.UNAUTHORIZED);
		}else {
			nonceCache.put(strs[1], strs[1]);
		}
	}
}
