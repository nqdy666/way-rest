package com.nd.gaea.waf.security.gaea;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>认证提供器</p>
 *
 * @author  yangz
 * @date    2016/1/7
 * @version latest
 */
public interface IAuthenticationProvider {

    /**
     * 获取认证token
     * @return
     */
    public String getAuthentication(HttpServletRequest request);
}
