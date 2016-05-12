package com.nd.gaea.waf.security.authentication.cookie;

import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationExtractor;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @author wukf
 * @date 2015/11/13
 * @since 2.0
 */
@Component
@Order(10)
public class PreAuthenticatedCookieTokenAuthenticationExtractor extends AbstractPreAuthenticatedAuthenticationExtractor {

    @Override
    public String getPrefix() {
        return "COOKIE";
    }

    @Override
    public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request) throws AuthenticationException {
        Assert.notNull(authenticationValue, "authenticationValue can not be null.");
        Map map = this.splitToMap(authenticationValue);
        long userId = Long.parseLong(this.getValue(map, "user_id"));
        Date expiresAt = new Date(Long.valueOf(this.getValue(map, "expires_at")));
        String realm = this.getValue(map, "realm");

        return new PreAuthenticatedCookieTokenAuthentication(userId, expiresAt, realm);
    }
}
