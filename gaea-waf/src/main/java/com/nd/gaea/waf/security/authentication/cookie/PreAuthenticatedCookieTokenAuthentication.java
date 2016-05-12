package com.nd.gaea.waf.security.authentication.cookie;

import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationToken;

import java.util.Date;

/**
 * @author wukf
 * @date 2015/11/13
 * @since 2.0
 */
public class PreAuthenticatedCookieTokenAuthentication extends AbstractPreAuthenticatedAuthenticationToken {
    private Date expiresAt;
    private long userId;
    private String realm;

    public PreAuthenticatedCookieTokenAuthentication(long userId, Date expiresAt, String realm) {
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.realm = realm;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
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
