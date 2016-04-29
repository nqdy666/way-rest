package com.nd.gaea.rest.security.services.impl;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.security.authens.UserCenterRoleDetails;
import com.nd.gaea.rest.security.authens.UserInfo;

/**
 * 用户信息获取缓存类
 *
 * @author 110825
 * @since 0.9.5
 */
public class UserCenterUserDetailsCacheService extends UserCenterUserDetailsService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserCenterUserDetailsCacheService.class);

    private static LoadingCache<String, List<UserCenterRoleDetails>> userRoleCache;

    private static LoadingCache<String, UserInfo> userInfoCache;

    private final String separator = ":";

    public static final String WAF_UC_CACHE_EXPIRE = "waf.uc.cache.expire";
    public static final String WAF_UC_CACHE_MAX_SIZE = "waf.uc.cache.maxSize";

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.put(WAF_UC_CACHE_MAX_SIZE, "10000");
        defaultProperties.put(WAF_UC_CACHE_EXPIRE, "20");
    }
    
    public LoadingCache<String, List<UserCenterRoleDetails>> getUserRoleCache(){
    	this.initUserRoleCache();
    	return userRoleCache;
    }
    
    public LoadingCache<String, UserInfo> getUserInfoCache(){
    	this.initUserInfoCache();
    	return userInfoCache;
    }

    /**
     * 调用父类获取用户角色信息的方法
     *
     * @param userId
     * @param realm
     * @return
     */
    private List<UserCenterRoleDetails> getUserRoleListInner(String userId, String realm) {
        return super.getUserRoleList(userId, realm);
    }

    /**
     * 调用父类获取用户信息的方法
     *
     * @param userId
     * @param realm
     * @return
     */
    private UserInfo getUserInfoInner(String userId, String realm) {
        return super.getUserInfo(userId, realm);
    }

    /**
     * 初始化userRole cache
     */
    private void initUserRoleCache() {
        if (userRoleCache == null) {
            synchronized (this) {
                if (userRoleCache == null) {
                    userRoleCache = CacheBuilder.newBuilder()
                            .maximumSize(WafProperties.getPropertyForInteger(WAF_UC_CACHE_MAX_SIZE))
                            .expireAfterWrite(WafProperties.getPropertyForInteger(WAF_UC_CACHE_EXPIRE), TimeUnit.MINUTES)
                            .build(new CacheLoader<String, List<UserCenterRoleDetails>>() {
                                @Override
                                public List<UserCenterRoleDetails> load(String key)
                                        throws Exception {
                                    String[] userIdAndRealm = key.split(separator);
                                    if (userIdAndRealm.length == 2) {
                                        return getUserRoleListInner(userIdAndRealm[0], userIdAndRealm[1]);
                                    } else {
                                        throw new IllegalArgumentException("initUserRoleCache 方法在获取 userId 和 realm 的过程发生异常。");
                                    }
                                }
                            });
                }
            }
        }
    }

    /**
     * 初始化userinfo cache
     */
    private void initUserInfoCache() {
        if (userInfoCache == null) {
            synchronized (this) {
                if (userInfoCache == null) {
                    userInfoCache = CacheBuilder.newBuilder()
                            .maximumSize(WafProperties.getPropertyForInteger(WAF_UC_CACHE_MAX_SIZE))
                            .expireAfterWrite(WafProperties.getPropertyForInteger(WAF_UC_CACHE_EXPIRE), TimeUnit.MINUTES)
                            .build(new CacheLoader<String, UserInfo>() {
                                @Override
                                public UserInfo load(String key) throws Exception {
                                    String[] userIdAndRealm = key.split(separator);
                                    if (userIdAndRealm.length == 2) {
                                        return getUserInfoInner(userIdAndRealm[0], userIdAndRealm[1]);
                                    } else {
                                        throw new IllegalArgumentException("initUserInfoCache 方法在获取 userId 和 realm 的过程发生异常。");
                                    }
                                }
                            });
                }
            }
        }
    }

    /**
     * 获取用户用户角色信息
     *
     * @return
     */
    public List<UserCenterRoleDetails> getUserRoleList(String userId, String realm) {
        this.initUserRoleCache();
        String key = userId + separator + realm;
        List<UserCenterRoleDetails> userCenterRoleDetails;
		try {
			userCenterRoleDetails = userRoleCache.get(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
        	userCenterRoleDetails = super.getUserRoleList(userId, realm);
        	if (userCenterRoleDetails!=null) {
        		userRoleCache.put(key, userCenterRoleDetails);
			}
		}
        return userCenterRoleDetails;
    }

    /**
     * 向UC发送获取用户信息的请求
     *
     * @return
     */
    public UserInfo getUserInfo(String userId, String realm) {
        this.initUserInfoCache();
        String key = userId + separator + realm;
        UserInfo userInfo;
		try {
			userInfo = userInfoCache.get(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			userInfo = super.getUserInfo(userId, realm);
        	if (userInfo!=null) {
        		userInfoCache.put(key, userInfo);
			}
		}
        return userInfo;
    }
}
