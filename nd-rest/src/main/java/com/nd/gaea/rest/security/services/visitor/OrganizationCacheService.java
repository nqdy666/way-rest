package com.nd.gaea.rest.security.services.visitor;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.security.authens.Organization;
import com.nd.gaea.rest.security.services.impl.CacheUtil;

/**
 * 组织服务实现类
 * @author Administrator
 * @since 0.9.6
 */
public class OrganizationCacheService extends OrganizationService {
	
	private static LoadingCache<String, Organization> organizationCache;

    public static final String WAF_ORGANIZATION_CACHE_EXPIRE = "waf.organization.cache.expire";
    public static final String WAF_ORGANIZATION_CACHE_MAX_SIZE = "waf.organization.cache.maxSize";

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.put(WAF_ORGANIZATION_CACHE_EXPIRE, "365");
        defaultProperties.put(WAF_ORGANIZATION_CACHE_MAX_SIZE, "2000");
    }
    
    public LoadingCache<String, Organization> getOrganizationCache(){
    	this.initOrganizationCache();
    	return organizationCache;
    }

    private void initOrganizationCache() {
        if (organizationCache == null) {
            synchronized (this) {
                if (organizationCache == null) {
                	organizationCache = CacheBuilder.newBuilder()
                            .maximumSize(WafProperties.getPropertyForInteger(WAF_ORGANIZATION_CACHE_MAX_SIZE))
                            .expireAfterWrite(WafProperties.getPropertyForInteger(WAF_ORGANIZATION_CACHE_EXPIRE), TimeUnit.DAYS)
                            .build(new CacheLoader<String, Organization>() {
                                @Override
                                public Organization load(String key) throws Exception {
                                    return invoke(key);
                                }
                            });
                }
            }
        }
    }
    
    /**
     * 调用uc接口查询组织信息
     * @param orgName
     * @return
     */
    protected Organization invoke(String orgName){
    	return super.query(orgName);
    }

	@Override
	public Organization query(String orgName) {
		Assert.notNull(orgName, "orgName");
        //初始化缓存实现
        this.initOrganizationCache();
        return CacheUtil.get(organizationCache, orgName);
	}
}
