/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.authens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统通过用户携带的Authorization头信息，验证通过后，封装用户认证信息。包括：<br>
 * 1、用户details
 * 2、用户authority
 * 3、用户token的有效性
 *
 * @author johnny
 */
public class WafUserAuthentication extends WafAbstractAuthenticationToken {
    
	private static final long serialVersionUID = -1737019866360703072L;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Object token;

    public WafUserAuthentication(List<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public WafUserAuthentication(UserCenterUserDetails userDetails) {
        this(userDetails.getAuthorities());
        setDetails(userDetails);
        setAuthenticated(true);
    }
    
    public WafUserAuthentication(UserCenterUserDetails userDetails, Object token) {
    	this(userDetails);
    	this.token = token;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        UserCenterUserDetails details = (UserCenterUserDetails) super.getDetails();
        if (details != null){
        	@SuppressWarnings("unchecked")
			List<GrantedAuthority> authorities = (List<GrantedAuthority>) details.getAuthorities();
        	if(authorities!=null && authorities.size()>0){
        		for (int i = 0; i < authorities.size();) {
    				if (authorities.get(i)==null) {
    					authorities.remove(i);
    					logger.error("getAuthorities():remove authorities["+i+"] is null,userId:"+((UserInfo)this.getPrincipal()).getUserId());
    				}else {
    					i++;
    				}
    			}
        		return authorities;
        	}
        }
        return new ArrayList<GrantedAuthority>();
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    // 返回用户主体信息,主体信息中包含用户信息 userinfo 和角色信息
    @Override
    public Object getPrincipal() {
        UserCenterUserDetails details = (UserCenterUserDetails) super
                .getDetails();
        if (details != null) {
            return details.getUserInfo();
        }
        return null;
    }

    public void setToken(Object token) {
        this.token = token;
    }

    public Object getToken() {
        return token;
    }


}
