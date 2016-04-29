package com.nd.gaea.client.support;

import com.nd.gaea.client.ApplicationContextUtil;
import com.nd.gaea.client.auth.WafBearerTokenProvider;
import com.nd.gaea.client.entity.WafBearerToken;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * bearer token处理类
 */
public class WafClientContextHolder {

    private static WafBearerTokenProvider provider;

    public static void setProvider(WafBearerTokenProvider provider) {
        WafClientContextHolder.provider = provider;
    }

    public static WafBearerTokenProvider getProvider() {
        if (provider == null) {
            ApplicationContext context = ApplicationContextUtil.getApplicationContext();
            Assert.notNull(context, "Application context cannot be null.");
            provider = context.getBean(WafBearerTokenProvider.class);
        }
        return provider;
    }

    /**
     * 获取bearer_token
     *
     * @return WafBearerToken
     * @throws
     */
    public static WafBearerToken getToken() {
        return getProvider().getToken();
    }

    /**
     * 判断是否经过授权
     *
     * @return boolean
     */
    public static boolean isAuthorized() {
        return getProvider().isAuthorized();
    }


    /**
     * 判断是否经过授权
     *
     * @return
     * @deprecated 请使用 {@link #isAuthorized}
     */
    @Deprecated
    public static boolean isAuthorize() {
        return isAuthorized();
    }
}
