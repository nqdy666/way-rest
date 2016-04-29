package com.nd.gaea.rest.security.services.impl;

import com.nd.gaea.rest.security.authens.UserCenterRoleDetails;
import com.nd.gaea.rest.security.authens.UserInfo;
import com.nd.gaea.rest.security.services.WafUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * 用户信息和角色信息代理服务提供类
 *
 * @author Administrator
 * @since 0.9.5
 */
public class UserCenterUserDetailsProxyService extends
        UserCenterUserDetailsCacheService {

    private WafUserDetailsService wafUserDetailsService;

    @Autowired
    @Qualifier("origin_waf_user_details_service")
    public void setWafUserDetailsService(WafUserDetailsService wafUserDetailsService)
    {
        this.wafUserDetailsService = wafUserDetailsService;
    }

    public List<UserCenterRoleDetails> getUserRoleList(String userId, String realm) {
        return new UserCenterRoleDetailsListProxy(userId, realm, wafUserDetailsService);
    }


    public UserInfo getUserInfo(String userId, String realm) {
        return new UserInfoProxy(userId, realm, wafUserDetailsService);
    }
}
