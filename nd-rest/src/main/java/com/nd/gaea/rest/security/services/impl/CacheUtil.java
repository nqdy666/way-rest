package com.nd.gaea.rest.security.services.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import com.nd.gaea.client.WafResourceAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author vime
 * @since 0.9.6
 */
public class CacheUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);
	
    public static <Key, Value> Value get(LoadingCache<Key, Value> cache, Key key) {
        try {
            return cache.get(key);
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            Throwable cause = e.getCause();
            if(cause != null) {
                // 如果是返回远程服务的错误，则返回该错误
                if (cause instanceof WafResourceAccessException)
                    throw (WafResourceAccessException) cause;
                if (cache instanceof RuntimeException)
                    throw (RuntimeException) cache;
            }
            throw new RuntimeException(e);
        }
    }

    public static <Key, Value> Value get(Cache<Key, Value> cache, Key key, Callable<? extends Value> valueLoader) {
        try {
            return cache.get(key, valueLoader);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Throwable cause = e.getCause();
            if(cause != null) {
                // 如果是返回远程服务的错误，则返回该错误
                if (cause instanceof WafResourceAccessException)
                    throw (WafResourceAccessException) cause;
                if (cache instanceof RuntimeException)
                    throw (RuntimeException) cache;
            }
            throw new RuntimeException(e);
        }
    }
}
