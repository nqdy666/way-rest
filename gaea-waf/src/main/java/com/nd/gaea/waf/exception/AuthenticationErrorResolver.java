package com.nd.gaea.waf.exception;

import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.exceptions.WafErrorResolver;
import com.nd.gaea.waf.security.gaea.GaeaContext;
import org.springframework.core.Ordered;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * 为浏览器请求提供授权或鉴权错误的重定向机制
 * Created by vime on 2016/2/24.
 */
public class AuthenticationErrorResolver implements WafErrorResolver, Ordered {
    public static final String GAEA_AUTH_REDIRECT_URL = "gaea.auth.redirectUrl";
    public static final String GAEA_AUTH_REDIRECT_DISABLED = "gaea.auth.redirectDisabled";

    static {
        Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.put(GAEA_AUTH_REDIRECT_DISABLED, "false");
    }

    @Override
    public boolean process(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
        if (!Boolean.parseBoolean(WafProperties.getProperty(GAEA_AUTH_REDIRECT_DISABLED)) && shouldProcess(throwable, request)) {
            try {
                redirect(request, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = WafProperties.getProperty(GAEA_AUTH_REDIRECT_URL);
        Assert.notNull(redirectUrl, "redirectUrl can not be null. Missing config for " + GAEA_AUTH_REDIRECT_URL);
        String url = java.net.URLEncoder.encode(request.getRequestURI(), "UTF-8");
        redirectUrl = MessageFormat.format(redirectUrl, url);
        response.sendRedirect(redirectUrl);
    }

    protected boolean shouldProcess(Throwable throwable, HttpServletRequest request) {
        boolean isTextHtml = GaeaContext.isTextHtml(request);
        boolean authException = throwable instanceof AuthenticationException || throwable instanceof AccessDeniedException;
        return isTextHtml && authException;
    }

    @Override
    public int getOrder() {
        return -10;
    }
}
