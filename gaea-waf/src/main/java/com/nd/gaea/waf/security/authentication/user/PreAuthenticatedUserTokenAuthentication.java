package com.nd.gaea.waf.security.authentication.user;

import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationToken;

/**
 * Created by vime on 2016/2/18.
 */
public class PreAuthenticatedUserTokenAuthentication extends AbstractPreAuthenticatedAuthenticationToken {
    private long userId;
    private String realm;

    public PreAuthenticatedUserTokenAuthentication(long userId, String realm) {
        this.userId = userId;
        this.realm = realm;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }
}
