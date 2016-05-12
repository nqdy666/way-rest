package com.nd.gaea.waf.security.authentication.user;

import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.rest.security.services.WafUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by vime on 2016/2/18.
 */
@Component
public class UserTokenAuthenticationProvider implements AuthenticationProvider {
    public static final String USER_TYPE = "User";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private WafUserDetailsService wafUserDetailsService;

    @Autowired
    public void setWafUserDetailsService(WafUserDetailsService wafUserDetailsService) {
        this.wafUserDetailsService = wafUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        this.logger.debug("User token authenticate begin");
        Assert.notNull(authentication, "authentication can not be null.");
        PreAuthenticatedUserTokenAuthentication token = (PreAuthenticatedUserTokenAuthentication) authentication;

        UserCenterUserDetails userDetails = wafUserDetailsService.loadUserDetailsByUserIdAndRealm(String.valueOf(token.getUserId()), token.getRealm());
        Assert.notNull(userDetails, "userDetails can not be null.");

        userDetails.getUserInfo().setUserType(USER_TYPE);

        this.logger.debug("User token authenticate end");
        return new WafUserAuthentication(userDetails, token);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == PreAuthenticatedUserTokenAuthentication.class;
    }
}

