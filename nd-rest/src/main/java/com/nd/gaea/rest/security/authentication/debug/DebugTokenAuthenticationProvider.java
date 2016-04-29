package com.nd.gaea.rest.security.authentication.debug;

import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.rest.security.services.WafUserDetailsService;
import com.nd.gaea.rest.support.WafContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vime
 * @since 0.9.5
 */
@Component
@Order(30)
public class DebugTokenAuthenticationProvider implements AuthenticationProvider {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private WafUserDetailsService ucUserDetailsService;

	@Autowired
	public void setUcUserDetailsService(WafUserDetailsService wafUserDetailsService) {
		this.ucUserDetailsService = wafUserDetailsService;
	}

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.debug("Debug token authenticate begin");
    	Assert.notNull(authentication, "authentication cannot be null.");
		if (!WafContext.isDebugMode())
			throw new RuntimeException("当前非调试模式，无法使用。请使用配置 " + WafContext.WAF_DEBUG_ENABLED + "=true");

		Assert.notNull(authentication, "Authentication is null.");
    	PreAuthenticatedDebugTokenAuthentication wafAuthentication = (PreAuthenticatedDebugTokenAuthentication) authentication;

    	logger.debug("get userDetials begin");

    	UserCenterUserDetails userDetails = ucUserDetailsService.loadUserDetailsByUserIdAndRealm(wafAuthentication.getUserId(), wafAuthentication.getRealm());
    	logger.debug("Get userDetials end");
		userDetails.getUserInfo().setUserType("Debug");

		logger.debug("Debug token authenticate end");
		return new WafUserAuthentication(userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == PreAuthenticatedDebugTokenAuthentication.class;
    }
}
