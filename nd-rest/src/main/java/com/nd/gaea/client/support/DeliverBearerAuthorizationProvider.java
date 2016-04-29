package com.nd.gaea.client.support;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nd.gaea.client.http.BearerAuthorizationProvider;
import com.nd.gaea.rest.security.authens.UserInfo;

/**
 * @author vime
 * @since 0.9.6
 */
public class DeliverBearerAuthorizationProvider implements BearerAuthorizationProvider {
	@Override
    public String getAuthorization() {
		return "Bearer \"" + WafClientContextHolder.getToken().getBearerToken() + "\"";
    }
	
	@Override
    public String getUserid() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = null;
		if (authentication!=null && authentication.getPrincipal()!=null){
			if (authentication.getPrincipal() instanceof UserInfo) {
				userId = ((UserInfo)authentication.getPrincipal()).getUserId();
			}
		}
		return userId;
    }
}
