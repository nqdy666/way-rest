package com.nd.gaea.rest.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nd.gaea.WafProperties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Eric
 * @ClassName: WafCorsFilter
 * @Description: 跨域资源共享过滤器
 * @date 2015年4月1日 下午3:52:36
 */
public class WafCorsFilter extends OncePerRequestFilter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String WAF_CORS_ALLOW_ORIGIN = "waf.cors.allow.origin";
	public static final String WAF_CORS_ALLOW_METHODS = "waf.cors.allow.methods";
	public static final String WAF_CORS_ALLOW_HEADERS = "waf.cors.allow.headers";
	public static final String WAF_CORS_MAX_AGE = "waf.cors.max.age";
	
	static{
		Properties defaultProperties = WafProperties.getDefaultProperties();
        defaultProperties.setProperty(WAF_CORS_ALLOW_ORIGIN, "*");
        defaultProperties.setProperty(WAF_CORS_ALLOW_METHODS, "GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE, PATCH");
        defaultProperties.setProperty(WAF_CORS_ALLOW_HEADERS, "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization, Cache-control, Orgname, vorg");
        defaultProperties.setProperty(WAF_CORS_MAX_AGE, "1800");
	}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("WAF CorsFilter doFilter start");

        // 设定CORS的初始化参数
        // cors.allowed.origins *:Any origin is allowed to access the resource
        response.addHeader("Access-Control-Allow-Origin", WafProperties.getProperty(WAF_CORS_ALLOW_ORIGIN));
        // cors.allowed.methods Access-Control-Allow-Methods: A comma separated
        // list of HTTP methods that can be used to access the resource
        response.addHeader("Access-Control-Allow-Methods", WafProperties.getProperty(WAF_CORS_ALLOW_METHODS));
        // cors.allowed.headers Access-Control-Allow-Headers: A comma separated
        // list of request headers that can be used when making an actual
        // request
        response.addHeader("Access-Control-Allow-Headers", WafProperties.getProperty(WAF_CORS_ALLOW_HEADERS));
        // cors.preflight.maxage Access-Control-Max-Age The amount of seconds,
        // browser is allowed to cache the result of the pre-flight request
        response.addHeader("Access-Control-Max-Age", WafProperties.getProperty(WAF_CORS_MAX_AGE));

        filterChain.doFilter(request, response);
		logger.debug("WAF CorsFilter doFilter end");
    }
}
