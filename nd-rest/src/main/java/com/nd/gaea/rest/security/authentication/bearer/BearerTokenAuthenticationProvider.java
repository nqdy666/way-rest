package com.nd.gaea.rest.security.authentication.bearer;

import com.nd.gaea.rest.config.AbstractWebSecurityConfigurerAdapter;
import com.nd.gaea.rest.security.authens.UserCenterRoleDetails;
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
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author vime
 * @since 0.9.5
 */
@Component
@Order(10)
public class BearerTokenAuthenticationProvider implements AuthenticationProvider {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private WafUserDetailsService userDetailsService;
    private TokenService bearerTokenService;

    @Autowired
    public void setWafUserDetailsService(WafUserDetailsService wafUserDetailsService) {
        this.userDetailsService = wafUserDetailsService;
    }

    @Autowired
    public void setTokenService(@Qualifier("bearer_token_service") TokenService tokenService) {
        this.bearerTokenService = tokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.debug("Bearer token authenticate begin");
        Assert.notNull(authentication, "authentication cannot be null.");
        PreAuthenticatedBearerTokenAuthentication bearerTokenAuthentication = (PreAuthenticatedBearerTokenAuthentication) authentication;

        //验证token
        WafUcCheckToken token = bearerTokenService.verifyToken(bearerTokenAuthentication);
        Assert.notNull(token, "token cannot be null.");
        logger.debug("authenticate token success");

        String userId = token.getUserId();
        boolean isServerUser = StringUtils.isEmpty(bearerTokenAuthentication.getUserId()) || userId.equals(bearerTokenAuthentication.getUserId());

        // check custom user,but not bearer user
        if (!isServerUser)
            userId = bearerTokenAuthentication.getUserId();

        UserCenterUserDetails userDetails = userDetailsService.loadUserDetailsByUserIdAndRealm(userId, bearerTokenAuthentication.getRealm());
        userDetails.getUserInfo().setUserType("Bearer");
        
     // 保证所有的接口都能使用 bearer 进行访问，而跳过具体的角色校验
        if (isServerUser) {
            List<UserCenterRoleDetails> list = (List<UserCenterRoleDetails>) userDetails.getAuthorities();
            //该判定是防止重复加入ROLE_BIZ_SERVER角色
            if (list.size() == 0 || !AbstractWebSecurityConfigurerAdapter.ROLE_BIZ_SERVER.equals(list.get(0).getRoleName())) {
            	UserCenterRoleDetails roleDetails = new UserCenterRoleDetails();
                roleDetails.setRoleName(AbstractWebSecurityConfigurerAdapter.ROLE_BIZ_SERVER);
                list.add(0, roleDetails);
            }
        }else{
        	userDetails.getUserInfo().setUserType("BearerDeliver");
        }


		logger.debug("Bearer token authenticate end");
        return new WafUserAuthentication(userDetails, token);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == PreAuthenticatedBearerTokenAuthentication.class;
    }
}
