package com.nd.gaea.client.support;

import com.nd.gaea.WafProperties;

import java.util.Properties;

/**
 * @author vime
 * @since 0.9.6
 */
public class WafContext {
    public static final String WAF_ENVIRONMENT = "waf.environment";
    public static final String WAF_UC_URI = "waf.uc.uri";
    public static final String WAF_UC_HOST = "waf.uc.host";
    /**
     * 指定当前是否启用调试模式，如果该值未指定则根据环境进行判断，非生产环境都认为启用调试模式
     *
     * @since 0.9.6
     */
    public static final String WAF_DEBUG_ENABLED = "waf.debug.enabled";
    /**
     * 指定当前是否启用跟踪，如果该值未指定则使用 waf.debug.enabled 的取值
     *
     * @since 0.9.6
     */
    public static final String WAF_TRACE_ENABLED = "waf.trace.enabled";

    private static final String WAF_ENVIRONMENT_PRODUCT = "product";
    private static final String WAF_UC_URI_VALUE = "https://aqapi.101.com/v0.93/";


    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.put(WAF_ENVIRONMENT, WAF_ENVIRONMENT_PRODUCT);
        defaultProperties.put(WAF_UC_URI, WAF_UC_URI_VALUE);
    }

    public static String getEnvironment() {
        return WafProperties.getProperty(WAF_ENVIRONMENT);
    }

    public static void setEnvironment(String env) {
        WafProperties.setProperty(WAF_ENVIRONMENT, env);
    }

    public static boolean isProductStage() {
        return getEnvironment().equalsIgnoreCase(WAF_ENVIRONMENT_PRODUCT);
    }

    /**
     * 是否为调试模式，可以使用 waf.debug.enabled=true|false 指定。默认值与 waf.environment 相关，仅在 waf.environment=product 为 false。
     *
     * @return 是否为调试模式
     * @since 0.9.6
     */
    public static boolean isDebugMode() {
        String debug = WafProperties.getProperty(WAF_DEBUG_ENABLED);
        if (debug != null)
            return Boolean.parseBoolean(debug);
        return !isProductStage();
    }

    /**
     * 获得当前跟踪功能是否可用。优先判断配置 waf.trace.enabled，如果未配置按 isDebugMode 来判断
     *
     * @return
     * @since 0.9.6
     */
    public static boolean isTraceEnabled() {
        String trace = WafProperties.getProperty(WAF_TRACE_ENABLED);
        if (trace != null){
        	return Boolean.parseBoolean(trace);
        }
        return false;
    }
}
