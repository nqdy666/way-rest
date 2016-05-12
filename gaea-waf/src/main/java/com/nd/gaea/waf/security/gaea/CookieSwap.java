package com.nd.gaea.waf.security.gaea;

import javax.servlet.http.Cookie;

/**
 * Created by Administrator on 2016/4/5.
 */
public class CookieSwap {

    public static final String CUSTOM_ID = "custom_id";

    public static String getCustomId(){
        return getValue(CUSTOM_ID);
    }

    public static String getValue(String key){
        Cookie[] cookies = GaeaContext.getRequest().getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if(name.equals(key)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
