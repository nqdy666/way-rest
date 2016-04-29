package com.nd.gaea.client.http;

/**
 * @author vime
 * @since 0.9.6
 */
public interface BearerAuthorizationProvider {
	
	String USERID = "Userid";
	
    public String getAuthorization();
    
    public String getUserid();
}
