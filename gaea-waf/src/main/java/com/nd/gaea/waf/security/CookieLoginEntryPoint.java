package com.nd.gaea.waf.security;

import com.google.common.base.Strings;
import com.nd.gaea.WafProperties;
import com.nd.gaea.client.WafResourceAccessException;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.rest.security.services.RealmService;
import com.nd.gaea.rest.security.services.WafUserDetailsService;
import com.nd.gaea.waf.security.authentication.cookie.CookieTokenAuthenticationProvider;
import com.nd.gaea.waf.security.gaea.provider.CookieAuthenticationProvider;
import com.nd.gaea.waf.security.service.IMacTokenProviderService;
import com.nd.gaea.waf.security.service.WafMacToken;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.Date;

/**
 * <p>gaea-waf cookie登录连接点</p>
 *
 * @author yangz
 * @version latest
 * @date 2016/1/25
 */
@Component
public class CookieLoginEntryPoint {
    public static final String GAEA_AUTHORIZATION_COOKIE_DOMAIN = "gaea.authorization.cookie.domain";

    private WafUserDetailsService wafUserDetailsService;
    private IMacTokenProviderService macTokenService;
    private RealmService realmService;

    @Autowired
    public void setWafUserDetailsService(WafUserDetailsService wafUserDetailsService) {
        this.wafUserDetailsService = wafUserDetailsService;
    }

    @Autowired
    public void setMacTokenService(IMacTokenProviderService macTokenService) {
        this.macTokenService = macTokenService;
    }

    @Autowired
    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    public ResponseEntity<ResponseErrorMessage> login(String loginName, String encodedPassword, String orgName, String domain, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(loginName, "loginName can not be null.");
        Assert.notNull(encodedPassword, "encodedPassword can not be null.");
        Assert.notNull(orgName, "orgName can not be null.");

        try {
            HashedMap<String, String> data = new HashedMap<String, String>();
            data.put("org_name", orgName);
            WafMacToken token = macTokenService.get(loginName, encodedPassword, data);

            String realm = realmService.getRealm(request);
            UserCenterUserDetails userDetails = wafUserDetailsService.loadUserDetailsByUserIdAndRealm(token.getUserId(), realm);
            Assert.notNull(userDetails, "userDetails cannot be null.");

            userDetails.getUserInfo().setUserType(CookieTokenAuthenticationProvider.USER_TYPE);
            SecurityContextHolder.getContext().setAuthentication(new WafUserAuthentication(userDetails));
            writeCookie(token.getUserId(), token.getExpiresAt(), realm, domain, response);

            return null;
        } catch (WafResourceAccessException e) {
            return e.getRemoteResponseEntity();
        }
    }

    /**
     * 登录
     */
    public ResponseEntity<ResponseErrorMessage> login(String loginName, String encodedPassword, String orgName, HttpServletRequest request, HttpServletResponse response) {
        return login(loginName, encodedPassword, orgName, WafProperties.getProperty(GAEA_AUTHORIZATION_COOKIE_DOMAIN), request, response);
    }

    /**
     * 注销
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(response);
        SecurityContextHolder.clearContext();
    }

    protected void writeCookie(String userId, Date expiresAt, String realm, String domain, HttpServletResponse response) {
        String trueRealm = "";
        if(realm != null){
            trueRealm = realm;
        }
        String dateTime = String.valueOf(expiresAt.getTime());
        String cookieValue = MessageFormat.format("COOKIE user_id=\"{0}\",expires_at=\"{1}\",realm=\"{2}\"", userId, dateTime, trueRealm);
        cookieValue = DESUtil.encode(cookieValue);
        Cookie cookie = new Cookie(CookieAuthenticationProvider.AUTHORIZATION, cookieValue);

        int maxAge = (int) ((expiresAt.getTime() - new Date().getTime()) / 1000);
        cookie.setMaxAge(maxAge);
        if (!Strings.isNullOrEmpty(domain))
            cookie.setDomain(domain);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    protected void deleteCookie(HttpServletResponse response) {
        Cookie cookieDel = new Cookie(CookieAuthenticationProvider.AUTHORIZATION, null);
        cookieDel.setMaxAge(0);
        cookieDel.setPath("/");
        response.addCookie(cookieDel);
    }
}
