/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.authentication;
/**
 * @author johnny
 *
 * 客户端通过请求携带的Authorization头信息的解析器。将字符串信息翻译成WafMacToken对象<br>
 */

import org.springframework.security.core.Authentication;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

public interface PreAuthenticatedAuthenticationExtractor {

    public String getPrefix();

    public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request) throws AuthenticationException;
}
