/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.security.services;

import com.nd.gaea.rest.security.authens.WafUcCheckToken;
import org.springframework.security.core.Authentication;


public interface TokenService {
	public WafUcCheckToken verifyToken(Authentication preAuthenticatedAuthentication) ;
}
