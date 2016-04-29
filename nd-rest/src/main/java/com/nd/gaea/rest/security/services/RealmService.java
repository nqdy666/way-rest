package com.nd.gaea.rest.security.services;

import javax.servlet.http.HttpServletRequest;

public interface RealmService {
	public String getRealm(HttpServletRequest request);
}
