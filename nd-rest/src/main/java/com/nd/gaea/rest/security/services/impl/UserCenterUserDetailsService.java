/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.services.impl;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.http.WafSecurityHttpClient;
import com.nd.gaea.rest.security.authens.UserCenterRoleDetails;
import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.UserInfo;
import com.nd.gaea.rest.security.services.WafUserDetailsService;
import com.nd.gaea.rest.support.WafContext;
import com.nd.gaea.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 用户详细信息的处理服务的实现类。
 *
 * @author johnny
 */
public class UserCenterUserDetailsService implements WafUserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String WAF_UC_GET_USER_INFO = "waf.uc.get.userInfo";

    public static final String WAF_UC_GET_USER_ROLES = "waf.uc.get.userRoles";

    private WafSecurityHttpClient wafSecurityHttpClient;

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.setProperty(WAF_UC_GET_USER_INFO, "users/{user_id}?realm={realm}");
        defaultProperties.setProperty(WAF_UC_GET_USER_ROLES, "users/{user_id}/roles?realm={realm}");
    }

    public String getUserInfoUrl() {
        return UrlUtil.combine(
                WafProperties.getProperty(WafContext.WAF_UC_URI),
                WafProperties.getProperty(WAF_UC_GET_USER_INFO)
        );
    }

    public String getUserRolesUrl() {
        return UrlUtil.combine(
                WafProperties.getProperty(WafContext.WAF_UC_URI),
                WafProperties.getProperty(WAF_UC_GET_USER_ROLES)
        );
    }

    @Autowired
    public void setWafSecurityHttpClient(WafSecurityHttpClient wafSecurityHttpClient)
    {
        this.wafSecurityHttpClient = wafSecurityHttpClient;
    }

    /**
     * 实现加在用户角色信息
     *
     * @return
     */
    public List<UserCenterRoleDetails> getUserRoleList(String userId,
                                                             String realm) {
    	Assert.notNull(userId, "userId");
    	Assert.notNull(realm, "realm");
        String userRolesUrl = this.getUserRolesUrl();

        // http://wiki.sdp.nd/index.php?title=UC_API_RestfulV0.9#.5BGET.5D.2Fusers.2F.7Buser_id.7D.2Froles.3Frealm.3Dxxx.C2.A0.E8.8E.B7.E5.8F.96.E7.94.A8.E6.88.B7.E8.A7.92.E8.89.B2
        UserCenterRoleDetailsResponse response = wafSecurityHttpClient.getForObject(userRolesUrl, UserCenterRoleDetailsResponse.class, userId, realm);
        return response.getItems(userId,realm);
    }

    /**
     * 向UC发送获取用户信息的请求
     *
     * @return
     */
    public UserInfo getUserInfo(String userId, String realm) {
    	Assert.notNull(userId, "userId");
    	String userInfoUrl = this.getUserInfoUrl();
        return wafSecurityHttpClient.getForObject(userInfoUrl, UserInfo.class, userId, realm);
    }

    @Override
    public UserCenterUserDetails loadUserDetailsByUserIdAndRealm(String userId, String realm) {
        // 1、获取用户信息
        UserInfo user = this.getUserInfo(userId, realm);
        logger.debug("user:{}, userId:{} ,realm:{}", user, userId, realm);

        // 2、获取角色信息
        List<UserCenterRoleDetails> roleDetailsCollection = null;
        if (!StringUtils.isEmpty(realm)){
        	roleDetailsCollection = this.getUserRoleList(userId, realm);
        }
        logger.debug("roleinfos:{}", roleDetailsCollection);

        // 3、封装details信息
        UserCenterUserDetails details = new UserCenterUserDetails(user, roleDetailsCollection);
        return details;
    }

    static class UserCenterRoleDetailsResponse
    {
    	private final Logger logger = LoggerFactory.getLogger(this.getClass());
        public List<UserCenterRoleDetails> getItems(String userId, String realm) {
        	if (items!=null) {
				for (int i = 0; i < items.size();) {
					if (items.get(i)==null) {
						items.remove(i);
						logger.error("getItems():userId:"+userId+",realm:"+realm+",items["+i+"] is null");
					}else {
						i++;
					}
				}
			}else {
				items = new ArrayList<UserCenterRoleDetails>();
			}
            return items;
        }

        public void setItems(List<UserCenterRoleDetails> items) {
            this.items = items;
        }

        private List<UserCenterRoleDetails> items;

    }
}
