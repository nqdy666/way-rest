/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.services;

import java.util.List;

import com.nd.gaea.rest.security.authens.UserCenterRoleDetails;
import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.UserInfo;

/**
 * 获取用户详细信息的服务接口。
 *
 * @author johnny
 */
public interface WafUserDetailsService {

    UserCenterUserDetails loadUserDetailsByUserIdAndRealm(String userId, String realm);

    UserInfo getUserInfo(String userId, String realm);

    List <UserCenterRoleDetails> getUserRoleList(String userId, String realm);

}
