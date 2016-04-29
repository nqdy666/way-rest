package com.nd.gaea.rest.security.authentication.mac;

import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationToken;

/**
 * @author vime
 * @since 0.9.5
 */
public class PreAuthenticatedMacTokenAuthentication extends AbstractPreAuthenticatedAuthenticationToken {

	private static final long serialVersionUID = 2743523648470140910L;
	
	private String id;
    private String mac;
    private String nonce;
    private String httpMethod;
    private String requestUri;
    private String host;
    private String realm;

    public PreAuthenticatedMacTokenAuthentication(String id, String mac, String nonce, String httpMethod, String requestUri, String host, String realm) {
        this.id = id;
        this.mac = mac;
        this.nonce = nonce;
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.host = host;
        this.realm = realm;
    }

	public String getId() {
        return id;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getMac() {
        return mac;
    }

    public String getNonce() {
        return nonce;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHost() {
        return host;
    }
    
    public String getRealm() {
		return realm;
	}

    @Override
    public int hashCode() {
        return id.hashCode();
    }

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
    
}
