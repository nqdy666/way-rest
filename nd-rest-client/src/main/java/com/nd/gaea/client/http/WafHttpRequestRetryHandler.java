package com.nd.gaea.client.http;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

public class WafHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler{

	public WafHttpRequestRetryHandler(){}
	
	/**
     * Create the request retry handler using the specified IOException classes
     *
     * @param retryCount how many times to retry; 0 means no retries
     * @param requestSentRetryEnabled true if it's OK to retry requests that have been sent
     */
	public WafHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled){
		super(retryCount, requestSentRetryEnabled);
	}
	
	/**
     * Create the request retry handler using the specified IOException classes
     *
     * @param retryCount how many times to retry; 0 means no retries
     * @param requestSentRetryEnabled true if it's OK to retry requests that have been sent
     * @param clazzes the IOException types that should not be retried
     */
	public WafHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled, final Collection<Class<? extends IOException>> clazzes){
		super(retryCount, requestSentRetryEnabled, clazzes);
	}
}
