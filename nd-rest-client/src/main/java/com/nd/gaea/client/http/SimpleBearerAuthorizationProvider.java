package com.nd.gaea.client.http;

import com.nd.gaea.client.support.WafClientContextHolder;

/**
 * @author vime
 * @since 0.9.6
 */
public class SimpleBearerAuthorizationProvider implements BearerAuthorizationProvider {
    @Override
    public String getAuthorization() {
        return "Bearer \"" + WafClientContextHolder.getToken().getBearerToken() + "\"";
    }

	@Override
	public String getUserid() {
		return null;
	}
}
