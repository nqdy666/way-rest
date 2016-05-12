package com.nd.gaea.waf.security.gaea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>认证token提供方管理</p>
 *
 * @author  yangz
 * @date    2016/1/7
 * @version latest
 */
public class AuthenticationProviderManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<IAuthenticationProvider> providers = new ArrayList<>();

    @Autowired
    public void setProviders(List<IAuthenticationProvider> providers)
    {
        Assert.notNull(providers, "providers can not be null.");
        this.providers = providers;
    }

    public void appendProvider(IAuthenticationProvider provider){
        Assert.notNull(provider, "provider can not be null.");
        providers.add(provider);
    }

    public String getAuthentication(HttpServletRequest request) throws AuthenticationException {
        for (IAuthenticationProvider extractor : providers) {
            String token = extractor.getAuthentication(request);
            if (!StringUtils.isEmpty(token)) {
                return token;
            }
        }
        return null;
    }
}
