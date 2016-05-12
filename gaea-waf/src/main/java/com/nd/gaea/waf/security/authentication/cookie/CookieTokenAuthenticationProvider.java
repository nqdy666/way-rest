package com.nd.gaea.waf.security.authentication.cookie;

import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.rest.security.services.WafUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;


/**
 * @author wukf
 * @date 2015/11/13
 * @since 2.0
 */
@Component
@Order(10)
public class CookieTokenAuthenticationProvider implements AuthenticationProvider {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String USER_TYPE = "COOKIE";

    private WafUserDetailsService wafUserDetailsService;

    @Autowired
    public void setWafUserDetailsService(WafUserDetailsService wafUserDetailsService) {
        this.wafUserDetailsService = wafUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("Cookie token authenticate begin");

        Assert.notNull(authentication, "authentication cannot be null.");
        PreAuthenticatedCookieTokenAuthentication cookieTokenAuthentication = (PreAuthenticatedCookieTokenAuthentication) authentication;

        // cookie is expires.
        if (getNow().after(cookieTokenAuthentication.getExpiresAt()))
            return null;

        UserCenterUserDetails userDetails = wafUserDetailsService.loadUserDetailsByUserIdAndRealm(String.valueOf(cookieTokenAuthentication.getUserId()), cookieTokenAuthentication.getRealm());
        Assert.notNull(userDetails, "userDetails cannot be null.");

        userDetails.getUserInfo().setUserType(USER_TYPE);

        logger.debug("Cookie token authenticate end");
        return new WafUserAuthentication(userDetails,cookieTokenAuthentication);
    }

    protected Date getNow(){
        return new Date();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == PreAuthenticatedCookieTokenAuthentication.class;
    }
}
