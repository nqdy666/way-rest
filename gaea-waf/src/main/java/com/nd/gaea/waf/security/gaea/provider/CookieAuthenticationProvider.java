package com.nd.gaea.waf.security.gaea.provider;

import com.nd.gaea.waf.security.DESUtil;
import com.nd.gaea.waf.security.gaea.IAuthenticationProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>cookie 认证方式</p>
 *
 * @author  yangz
 * @date    2016/1/11
 * @version latest
 */
@Component
@Order(71)
public class CookieAuthenticationProvider implements IAuthenticationProvider {

    public static final String AUTHORIZATION = "_G_Authorization_";

    @Override
    public String getAuthentication(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION)) {
                    return DESUtil.decode(cookie.getValue());
                }
            }
        }
        return null;
    }
}
