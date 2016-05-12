package com.nd.gaea.waf.security.service;

import com.google.common.collect.Maps;
import com.nd.gaea.WafProperties;
import com.nd.gaea.client.http.WafHttpClient;
import com.nd.gaea.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vime on 2016/2/18.
 */
public class UserCenterMacTokenProviderService implements IMacTokenProviderService {

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.setProperty("waf.uc.api.post_mac_token", "tokens");
    }

    @Autowired
    @Qualifier("wafHttpClient")
    private WafHttpClient httpClient;

    protected String getPostMacTokenUri(){
        return UrlUtil.combine(this.getProperty("waf.uc.uri"), this.getProperty("waf.uc.api.post_mac_token"));
    }

    protected String getRefreshMacTokenUri(){
        return UrlUtil.combine(this.getProperty("waf.uc.uri"), this.getProperty("waf.uc.api.refresh_token"));
    }

    @Override
    public WafMacToken get(String loginName, String encodedPassword, Map<String, String> datas) {
        Assert.notNull(loginName, "loginName can not be null.");
        Assert.notNull(encodedPassword, "encodedPassword can not be null.");

        HashMap<String, String> map = datas == null? new HashMap<String, String>(): Maps.newHashMap(datas);
        map.put("login_name", loginName);
        map.put("password", encodedPassword);

        String uri = getPostMacTokenUri();

        return httpClient.postForObject(uri, map, WafMacToken.class);
    }

    @Override
    public WafMacToken refresh(WafMacToken token) {
        Assert.notNull(token, "token can not be null.");
        String uri = getRefreshMacTokenUri();
        return (WafMacToken)httpClient.postForObject(uri, (Object)null, WafMacToken.class, token.getRefreshToken());
    }

    protected String encodePassword(String password)
    {
        return password;
    }

    private String getProperty(String key) {
        return WafProperties.getProperty(key);
    }
}
