package com.nd.gaea.client.auth;

import com.nd.gaea.client.entity.WafBearerToken;
import org.springframework.web.client.RestClientException;

/**
 * 服务器端 bearer_token 认证服务
 * @author vime
 * @since 0.9.5
 */
public interface WafBearerTokenService {
    /**
     * 获取token
     *
     * @return WafBearerToken
     * @throws RestClientException
     */
    WafBearerToken getBearerToken() throws RestClientException;

    /**
     * 刷新token
     *
     * @param wafBearerToken
     * @return WafBearerToken
     * @throws RestClientException
     */
    WafBearerToken refreshBearerToken(WafBearerToken wafBearerToken) throws RestClientException;
}
