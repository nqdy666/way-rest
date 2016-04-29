package com.nd.gaea.rest.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WAF 对异常进行处理的接口
 * @author vime
 * @since 0.9.6
 */
public interface WafErrorResolver {
    public boolean process(Throwable throwable, HttpServletRequest request, HttpServletResponse response);
}
