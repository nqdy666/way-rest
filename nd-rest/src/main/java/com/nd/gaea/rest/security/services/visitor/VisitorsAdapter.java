package com.nd.gaea.rest.security.services.visitor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author 110825
 * @since 0.9.8
 */
public interface VisitorsAdapter {
	
	public boolean isPermit(HttpServletRequest request, HttpServletResponse response, Object handler);
}
