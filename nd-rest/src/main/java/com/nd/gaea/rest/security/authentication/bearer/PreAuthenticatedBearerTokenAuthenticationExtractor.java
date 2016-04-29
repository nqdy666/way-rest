package com.nd.gaea.rest.security.authentication.bearer;

import com.nd.gaea.client.support.DeliverBearerAuthorizationProvider;
import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationExtractor;
import com.nd.gaea.rest.security.services.RealmService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author vime
 * @since 0.9.5
 */
@Component
@Order(10)
public class PreAuthenticatedBearerTokenAuthenticationExtractor extends AbstractPreAuthenticatedAuthenticationExtractor {
    
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private RealmService realmService;

    @Autowired
    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    @Override
    public String getPrefix() {
        return "BEARER";
    }

    @Override
    public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request) throws AuthenticationException {
        String realm = realmService.getRealm(request);
        String bearerToken = StringUtils.strip(authenticationValue, "\"");;
        Assert.hasText(bearerToken, "bearer token is missing.");
        String userId = request.getHeader(DeliverBearerAuthorizationProvider.USERID);
		logger.debug("bearerToken:{}, realm:{}, userId:{}", bearerToken, realm, userId);
        return new PreAuthenticatedBearerTokenAuthentication(bearerToken, userId, realm);
    }
}
