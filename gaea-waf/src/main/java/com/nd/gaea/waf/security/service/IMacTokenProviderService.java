package com.nd.gaea.waf.security.service;

import java.util.Map;

/**
 * Created by vime on 2016/2/18.
 */
public interface IMacTokenProviderService {
    WafMacToken get(String loginName, String password, Map<String,String> datas);

    WafMacToken refresh(WafMacToken token);
}
