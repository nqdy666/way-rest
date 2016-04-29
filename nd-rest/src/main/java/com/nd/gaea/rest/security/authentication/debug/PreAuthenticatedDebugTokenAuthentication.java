package com.nd.gaea.rest.security.authentication.debug;

import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationToken;

/**
 * @author vime
 * @since 0.9.5
 */
public class PreAuthenticatedDebugTokenAuthentication extends AbstractPreAuthenticatedAuthenticationToken {

	private static final long serialVersionUID = 6860284183764421468L;
	
	private String userId;
    private String realm;

    public PreAuthenticatedDebugTokenAuthentication(String userId, String realm) {
        this.userId = userId;
        this.realm = realm;
    }
    public String getUserId() {
        return userId;
    }

    public String getRealm() {
        return realm;
    }
}
