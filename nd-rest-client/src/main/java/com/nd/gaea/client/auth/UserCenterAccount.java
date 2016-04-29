package com.nd.gaea.client.auth;

import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 封装UC服务器端认证用户名和密码的对象
 * @author vime
 * @since 0.9.5
 */
class UserCenterAccount implements Serializable {
    private static final long serialVersionUID = 5632331017111432552L;
    private String loginName;
    private String password;

    public UserCenterAccount(String loginName, String password) {
        Assert.notNull(loginName, "loginName cannot be null.");
        Assert.notNull(password, "password cannot be null.");
        this.loginName = loginName;
        this.password = password;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }
}
