package com.nd.gaea.client.auth;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.entity.WafBearerToken;
import com.nd.gaea.client.http.WafHttpClient;
import com.nd.gaea.util.UrlUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClientException;

import java.util.Date;
import java.util.Properties;

import javax.annotation.Resource;

/**
 * 服务器端bearer_token认证，用户名和密码登陆uc实现
 *
 * @author vime
 * @since 0.9.5
 */
public class WafBearerTokenServiceImpl implements WafBearerTokenService {
    private static final String WAF_UC_URI = "waf.uc.uri";

    public static final String WAF_UC_API_REFRESH_TOKEN = "waf.uc.api.refresh_token";
    public static final String WAF_UC_API_POST_BEARER_TOKEN = "waf.uc.api.post_bearer_token";
    public static final String WAF_CLIENT_BEARER_TOKEN_ACCOUNT_USERNAME = "waf.client.bearerToken.account.username";
    public static final String WAF_CLIENT_BEARER_TOKEN_ACCOUNT_PASSWORD = "waf.client.bearerToken.account.password";

    public WafHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(WafHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    @Autowired
    @Qualifier("wafHttpClient")
    private WafHttpClient httpClient;

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.setProperty(WAF_UC_API_REFRESH_TOKEN, "tokens/{refresh_token}/actions/refresh");
        defaultProperties.setProperty(WAF_UC_API_POST_BEARER_TOKEN, "bearer_tokens");
        defaultProperties.setProperty(WAF_CLIENT_BEARER_TOKEN_ACCOUNT_USERNAME, "waf_loginer");
        defaultProperties.setProperty(WAF_CLIENT_BEARER_TOKEN_ACCOUNT_PASSWORD, "80fba977d063a6f7262a8a9c95f61140");
    }

    @Override
    public WafBearerToken getBearerToken() throws RestClientException {
        WafBearerToken wafBearerToken = null;
        String uri = getPostBearerTokenUri();
        UserCenterAccount ucAccount = getUCAccount();
        WafAccessToken accessToken = httpClient.postForObject(uri, ucAccount, WafAccessToken.class);
        if (accessToken != null) {
            wafBearerToken = accessToken.toWafBearerToken();
        }
        return wafBearerToken;
    }

    @Override
    public WafBearerToken refreshBearerToken(WafBearerToken wafBearerToken) throws RestClientException {
        WafBearerToken refreshBearerToken = null;
        String uri = getRefreshTokenUri();
        WafAccessToken accessToken = httpClient.postForObject(uri, null, WafAccessToken.class, wafBearerToken.getRefreshToken());
        if (accessToken != null) {
            refreshBearerToken = accessToken.toWafBearerToken();
        }
        return refreshBearerToken;
    }

    protected String getRefreshTokenUri() {
        return UrlUtil.combine(getProperty(WAF_UC_URI),getProperty(WAF_UC_API_REFRESH_TOKEN));
    }

    protected String getPostBearerTokenUri() {
        return UrlUtil.combine(getProperty(WAF_UC_URI),getProperty(WAF_UC_API_POST_BEARER_TOKEN));
    }

    protected UserCenterAccount getUCAccount() {
        return new UserCenterAccount(
                getProperty(WAF_CLIENT_BEARER_TOKEN_ACCOUNT_USERNAME),
                getProperty(WAF_CLIENT_BEARER_TOKEN_ACCOUNT_PASSWORD)
        );
    }

    private String getProperty(String key) {
        return WafProperties.getProperty(key);
    }

    private String getProperty(String key, String defaultValue) {
        return WafProperties.getProperty(key, defaultValue);
    }

    private static class WafAccessToken {
        private String userId;//应用ID
        private String accessToken;//接收uc提供的bearer_token值
        private Date expiresAt;//过期时间
        private String refreshToken;//过期后用于刷新的token

        public String getUserId() {
            return userId;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public Date getExpiresAt() {
            return expiresAt;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public WafBearerToken toWafBearerToken() {
            return new WafBearerToken(getUserId(), getAccessToken(), getRefreshToken(), getExpiresAt());
        }
    }
}



