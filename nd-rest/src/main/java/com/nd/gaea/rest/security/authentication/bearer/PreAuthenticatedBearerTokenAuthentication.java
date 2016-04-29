package com.nd.gaea.rest.security.authentication.bearer;

import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationToken;

import org.springframework.util.Assert;

/**
 * @author vime
 * @since 0.9.5
 */
public class PreAuthenticatedBearerTokenAuthentication extends AbstractPreAuthenticatedAuthenticationToken {

    private static final long serialVersionUID = 4835868017048568755L;

    private String bearerToken;
    private String realm;
    private String userId;

    public PreAuthenticatedBearerTokenAuthentication(String bearerToken, String userId, String realm) {
        Assert.hasText(bearerToken, "Bearer token should contains text.");
        this.bearerToken = bearerToken;
        this.userId = userId;
        this.realm = realm;
    }

    public String getRealm() {
        return realm;
    }

    public String getUserId() {
        return userId;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    @Override
    public int hashCode() {
        return bearerToken.hashCode();
    }

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
    
}
