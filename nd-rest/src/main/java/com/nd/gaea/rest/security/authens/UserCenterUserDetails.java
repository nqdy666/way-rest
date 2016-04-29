/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.authens;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户中心用户详细信息的详细数据封装对象。其实现WafUserDetails接口
 *
 * @author johnny
 */
public class UserCenterUserDetails {

    private UserInfo userInfo;
    private List<UserCenterRoleDetails> authorities;

    public UserCenterUserDetails(UserInfo userInfo, List<UserCenterRoleDetails> roles) {
        Assert.notNull(userInfo, "userInfo cannot be null.");
        this.userInfo = userInfo;
        this.authorities = roles;
        if (this.authorities == null)
            this.authorities = new ArrayList<>();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }


    public List<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }


}
