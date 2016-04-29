package com.nd.gaea.rest.security.authentication.mac;

import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.rest.security.services.TokenService;
import com.nd.gaea.rest.security.services.WafUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author vime
 * @since 0.9.5
 */
@Component
@Order(20)
public class MacTokenAuthenticationProvider implements AuthenticationProvider {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private WafUserDetailsService wafUserDetailsService;
	private TokenService macTokenService;


	@Autowired
	public void setWafUserDetailsService(WafUserDetailsService wafUserDetailsService) {
		this.wafUserDetailsService = wafUserDetailsService;
	}

	@Autowired
	public void setTokenService(@Qualifier("mac_token_service") TokenService tokenService) {
		this.macTokenService = tokenService;
	}

    /**
     * 进行认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	logger.debug("Mac token authenticate begin");
    	Assert.notNull(authentication, "authentication cannot be null.");
    	PreAuthenticatedMacTokenAuthentication macTokenAuthentication = (PreAuthenticatedMacTokenAuthentication) authentication;

		//验证token
		WafUcCheckToken token = macTokenService.verifyToken(macTokenAuthentication);
		Assert.notNull(token, "token cannot be null.");

		UserCenterUserDetails userDetails = wafUserDetailsService.loadUserDetailsByUserIdAndRealm(token.getUserId(), macTokenAuthentication.getRealm());

		if (userDetails != null)
        {
			userDetails.getUserInfo().setUserType("Mac");
		}
		Assert.notNull(userDetails, "userDetials cannot be null.");

		logger.debug("Mac token authenticate end");
		return new WafUserAuthentication(userDetails, token);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == PreAuthenticatedMacTokenAuthentication.class;
    }
}
