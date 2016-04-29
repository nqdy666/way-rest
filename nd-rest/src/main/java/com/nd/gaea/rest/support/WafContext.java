/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.support;

import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.security.authens.UserCenterRoleDetails;
import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.UserInfo;
import com.nd.gaea.rest.security.authens.WafUcCheckToken;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * 提供给系统的全局访问认证用户的信息。
 *
 * @author johnny
 */
public class WafContext extends com.nd.gaea.client.support.WafContext {
    /**
     * 当前安全模块的鉴权行为（判断当前用户具备范围某些资源的权限）是否被禁用
     *
     * @since 0.9.6
     */
    public static final String WAF_SECURITY_DISABLED = "waf.security.disabled";
    public static final String WAF_GUEST_ENABLED = "waf.guest.enabled";

    /**
     * 认证用户的相关详细信息，其中包括用户信息，角色信息，用户领域属性，用户区域属性以及主体和token信息
     */
    public static UserCenterUserDetails getCurrentDetails() {

        UserCenterUserDetails details = (UserCenterUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getDetails();
        return details;
    }

    @Deprecated
    public static UserInfo getCurrertUserInfo() {
        return getCurrentUserInfo();
    }

    /**
     * 获得当前登录用户的信息
     *
     * @return
     */
    public static UserInfo getCurrentUserInfo() {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userInfo;
    }

    /**
     * 获得当前登录用户的验证凭据，token
     *
     * @return
     */
    public static WafUcCheckToken getCurrentToken() {
        WafUcCheckToken token = (WafUcCheckToken) SecurityContextHolder
                .getContext().getAuthentication().getCredentials();
        return token;
    }

    /**
     * 获得当前用户的登录角色信息
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<UserCenterRoleDetails> getCurrentUserRoles() {
        List<UserCenterRoleDetails> roles = (List<UserCenterRoleDetails>) SecurityContextHolder
                .getContext().getAuthentication().getAuthorities();
        return roles;
    }


    /**
     * 获取当前安全模块的鉴权行为（判断当前用户具备范围某些资源的权限）是否被禁用。可以通过 waf.properties 配置 waf.security.disabled=true|false 进行设置，默认 false。
     *
     * @return
     */
    public static boolean isSecurityDisabled() {
        return Boolean.parseBoolean(WafProperties.getProperty(WAF_SECURITY_DISABLED));
    }
}
