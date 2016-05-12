package com.nd.gaea.waf.security.gaea.provider;

import com.nd.gaea.waf.security.gaea.GaeaUserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by Administrator on 2016/3/23.
 */
@Component
public class GaeaAuthenticationProvider implements AuthenticationProvider {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        this.logger.debug("gaea token authenticate begin");
        Assert.notNull(authentication, "authentication can not be null.");
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == GaeaUserAuthentication.class;
    }
}
