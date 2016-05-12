package com.nd.gaea.waf.security.gaea;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.rest.security.services.TokenService;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * Created by vime on 2016/2/19.
 */
public class GaeaTokenParser {
    public static final String GAEA_AUTHORIZATION = "X-Gaea-Authorization";

    private TokenService bearerTokenService;

    public GaeaTokenParser(TokenService bearerTokenService) {
        this.bearerTokenService = bearerTokenService;
    }

    public GaeaToken parse(HttpServletRequest servletRequest) {
        // from gaea token
        String gaeaAuthorization = servletRequest.getHeader(GAEA_AUTHORIZATION);
        if(Strings.isNullOrEmpty(gaeaAuthorization)){
            Cookie[] cookies = servletRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(GAEA_AUTHORIZATION)) {
                        gaeaAuthorization = "GAEA id=\"" + cookie.getValue() + "\"";
                    }
                }
            }
        }
        if (!Strings.isNullOrEmpty(gaeaAuthorization)) {
            if (gaeaAuthorization.length() <= 5)
                throw new RuntimeException("Gaea authorization format error." + gaeaAuthorization);
            gaeaAuthorization = gaeaAuthorization.substring(5);
            Map<String, String> map = splitToMap(gaeaAuthorization);
//            PreAuthenticatedBearerTokenAuthentication bearerTokenAuthentication = new PreAuthenticatedBearerTokenAuthentication(getValue(map, "id"), null, null);
//            WafUcCheckToken ucCheckToken = bearerTokenService.verifyToken(bearerTokenAuthentication);
            return new GaeaToken(null, getValue(map, "id"));
        }
        // from bearer token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication instanceof WafUserAuthentication) {
            WafUserAuthentication wafUserAuthentication = (WafUserAuthentication) authentication;
            UserCenterUserDetails userDetails = (UserCenterUserDetails) wafUserAuthentication.getDetails();
            if (userDetails != null && userDetails.getUserInfo().getUserType().startsWith("Bearer")) {
                WafUcCheckToken ucCheckToken = (WafUcCheckToken) wafUserAuthentication.getToken();
                return new GaeaToken(ucCheckToken.getBearerToken(), Long.parseLong(ucCheckToken.getUserId()));
            }
        }
        // from local bearer config
        return null;
    }

    protected Map<String, String> splitToMap(String data) {
        return new CaseInsensitiveMap(Splitter.on(",").trimResults().withKeyValueSeparator(Splitter.on("=").trimResults().limit(2)).split(data));
    }

    protected String getValue(Map<String, String> map, String name) {
        String value = (String) map.get(name);
        if (value != null) {
            value = StringUtils.strip(value, "\"");
        }

        return value;
    }
}
