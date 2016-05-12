package com.nd.gaea.waf.security.gaea;

import com.nd.gaea.client.entity.WafBearerToken;
import com.nd.gaea.client.support.WafClientContextHolder;
import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.UserInfo;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.waf.security.DESUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * gaea auth context
 * Created by vime on 2016/2/19.
 */
public class GaeaContext {
    public static final String LOCAL_REALM = "LOCAL_REALM";
    public static final String LOCAL_REQUEST = "LOCAL_REQUEST";
    public static final String LOCAL_RESPONSE = "LOCAL_RESPONSE";
    public static final String CALL_TYPE_FIELD = "CALL_TYPE_FIELD";
    public static final String CALL_TYPE_VALUE_GAEA = "CALL_TYPE_VALUE_GAEA";
    public static final String CALL_TYPE_VALUE_SDP = "CALL_TYPE_VALUE_SDP";
    public static final String ZUUL_TYPE_FIELD = "ZUUL_TYPE_FIELD";
    public static final String ZUUL_TYPE_VALUE_CLOUD = "ZUUL_TYPE_VALUE_CLOUD";
    public static final String ZUUL_TYPE_VALUE_GAEA = "ZUUL_TYPE_VALUE_GAEA";
    private static ThreadLocal<HashMap> local = new ThreadLocal<>();

    public static UserInfo getUserInfo() {
        WafUserAuthentication wafUserAuthentication = getAuthentication();
        if (wafUserAuthentication != null) {
            UserCenterUserDetails userDetails = (UserCenterUserDetails) wafUserAuthentication.getDetails();
            if (userDetails != null)
                return userDetails.getUserInfo();
        }
        return null;
    }

    public static WafUserAuthentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication instanceof WafUserAuthentication)
            return (WafUserAuthentication) authentication;
        return null;
    }

    public static Long getUserId() {
        UserInfo userInfo = getUserInfo();
        if (userInfo != null)
            return Long.parseLong(userInfo.getUserId());
        return null;
    }

    public static GaeaToken getGaeaToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication instanceof GaeaUserAuthentication) {
            GaeaUserAuthentication gaeaUserAuthentication = (GaeaUserAuthentication) authentication;
            return gaeaUserAuthentication.getGaeaToken();
        }
        WafBearerToken token = WafClientContextHolder.getToken();
        return new GaeaToken(token.getBearerToken(),Long.parseLong(token.getUserId()));
    }

    public static Long getAppId() {
        GaeaToken gaeaToken = getGaeaToken();
        if (gaeaToken != null)
            return gaeaToken.getGaeaId();
        return null;
    }

    /**
     * 获取基础平台加密串,嵌基础平台页面专用
     * 跟在URL上面, key = cloud_token
     *
     * @return
     */
    public static String getCloudToken(){
        String result = DESUtil.encode(getAppId() + "," + getUserId());
        return result;
    }

    protected static HashMap getLocal() {
        HashMap hashMap = local.get();
        if (hashMap == null) {
            hashMap = new HashMap();
            local.set(hashMap);
        }
        return hashMap;
    }

    public static <T> T get(String key, Class<T> clazz) {
        return (T) getLocal().get(key);
    }

    public static void put(String key, Object value) {
        getLocal().put(key, value);
    }

    public static void clear(String key) {
        getLocal().put(key, null);
    }

    public static String getRealm() {
        return get(LOCAL_REALM, String.class);
    }

    public static void setRealm(String realm) {
        put(LOCAL_REALM, realm);
    }

    public static HttpServletRequest getRequest() {
        return get(LOCAL_REQUEST, HttpServletRequest.class);
    }

    public static HttpServletResponse getResponse() {
        return get(LOCAL_RESPONSE, HttpServletResponse.class);
    }

    public static boolean isTextHtml(HttpServletRequest request){
        Assert.notNull(request, "request can not be null.");
        String accept = request.getHeader("ACCEPT");
        return accept!= null && accept.contains("text/html");
    }

    public static boolean isTextHtml(){
        return isTextHtml(getRequest());
    }
}
