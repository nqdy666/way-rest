package com.nd.gaea.rest.security.services;

import org.springframework.security.authentication.AuthenticationProvider;

public interface AuthenticationProviderService {
			
	public AuthenticationProvider getTestAuthenticationProvider();
	
	public AuthenticationProvider getAuthenticationProvider();
}
