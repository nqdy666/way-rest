package com.nd.gaea.rest.security.services.impl;

import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.security.services.RealmService;

import javax.servlet.http.HttpServletRequest;

public class RealmServiceImpl implements RealmService {

    /**
     * 标识当前用户的业务领域，当获取用户及角色信息时需要
     *
     * @since 0.9.6
     */
    public final static String WAF_UC_REALM = "waf.uc.realm";

    @Override
    public String getRealm(HttpServletRequest request) {
        return WafProperties.getProperty(WAF_UC_REALM);
    }

}
