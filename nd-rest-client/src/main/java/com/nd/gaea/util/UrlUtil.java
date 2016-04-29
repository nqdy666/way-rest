package com.nd.gaea.util;

import java.net.URL;

/**
 * @author vime
 * @since 0.9.5
 */
public class UrlUtil {
    public static String combine(String baseUrl, String relativeUrl) {
        try {
            URL url = new URL(new URL(baseUrl), relativeUrl);
            return url.toString();
        } catch (Exception ex) {
            throw new RuntimeException("将 \"" + baseUrl + "\" 与 \"" + relativeUrl + "\" 合并 URL 过程发生 " + ex.getMessage() + " 错误", ex);
        }
    }
}
