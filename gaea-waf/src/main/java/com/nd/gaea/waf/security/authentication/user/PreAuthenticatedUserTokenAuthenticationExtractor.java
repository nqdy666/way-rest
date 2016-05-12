package com.nd.gaea.waf.security.authentication.user;

import com.google.common.base.Strings;
import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationExtractor;
import com.nd.gaea.rest.security.services.RealmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by vime on 2016/2/18.
 */
@Component
public class PreAuthenticatedUserTokenAuthenticationExtractor extends AbstractPreAuthenticatedAuthenticationExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private RealmService realmService;

    @Autowired
    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    @Override
    public String getPrefix() {
        return "USER";
    }

    @Override
    public Authentication extractAuthentication(String authenticationValue, HttpServletRequest httpServletRequest) throws AuthenticationException {
        Assert.notNull(authenticationValue, "authenticationValue can not be null.");

        Map map = this.splitToMap(authenticationValue);
        String userId = this.getValue(map, "user_id");
        String realm = this.getValue(map, "realm");

        Assert.hasText(userId, "User token property user_id is missing.");
        if (Strings.isNullOrEmpty(realm))
            realm = realmService.getRealm(httpServletRequest);
        Assert.hasText(userId, "User token property realm is missing.");

        return new PreAuthenticatedUserTokenAuthentication(Long.parseLong(userId), realm);
    }
}
