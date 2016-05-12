package com.nd.gaea.waf.security.gaea.provider;

import com.nd.gaea.waf.security.gaea.IAuthenticationProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>请求头认证提供者</p>
 *
 * @author  yangz
 * @date    2016/1/11
 * @version latest
 */
@Component
@Order(70)
public class HeaderAuthenticationProvider implements IAuthenticationProvider {

    public static final String AUTHORIZATION = "Authorization";

    @Override
    public String getAuthentication(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }
}
