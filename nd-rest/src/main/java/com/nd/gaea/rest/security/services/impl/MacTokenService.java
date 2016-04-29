package com.nd.gaea.rest.security.services.impl;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;

import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import com.nd.gaea.rest.security.authentication.PreAuthenticatedAuthenticationExtractorManager;
import com.nd.gaea.rest.security.authentication.mac.PreAuthenticatedMacTokenAuthenticationExtractor;
import com.nd.gaea.rest.security.services.TokenService;

/**
 * 提供给外部系统调用MacToken认证类
 * @author 110825
 * @since 0.9.7
 */
public class MacTokenService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private TokenService tokenService;

	@Autowired
	public void setTokenService(@Qualifier("mac_token_service") TokenService tokenService) {
		this.tokenService = tokenService;
	}
	
	@Autowired
	private PreAuthenticatedMacTokenAuthenticationExtractor preAuthenticatedMacTokenAuthenticationExtractor;
	@Autowired
	private PreAuthenticatedAuthenticationExtractorManager extractorManager;
	
	/**
	 * mac认证方法
	 * @param request
	 * @return
	 * @throws AuthenticationException
	 */
	public WafUcCheckToken authenticate(HttpServletRequest request) throws AuthenticationException{
		WafUcCheckToken wafUcCheckToken = null;
		String authorization = request.getHeader("Authorization");
		logger.debug("Authorization: {}", authorization);
		if (authorization != null) {
			Authentication preAuthenticatedAuthentication = extractorManager.extractAuthentication(authorization, request);
			wafUcCheckToken = tokenService.verifyToken(preAuthenticatedAuthentication);
		}
		return wafUcCheckToken;
	}
	
	/**
	 * 是否关闭WAF的mac认证，默认false不关闭
	 * @param request
	 * @return
	 */
	public boolean disableMacTokenAuthentication(HttpServletRequest request){
		return false;
	}
}
