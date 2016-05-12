package com.nd.gaea.waf.security.gaea;

import com.nd.gaea.SR;
import com.nd.gaea.WafException;
import com.nd.gaea.client.WafResourceAccessException;
import com.nd.gaea.rest.filter.TokenAuthenticationProcessFilter;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.rest.security.authentication.PreAuthenticatedAuthenticationExtractorManager;
import com.nd.gaea.rest.security.authentication.WafAuthenticationException;
import com.nd.gaea.rest.security.services.RealmService;
import com.nd.gaea.waf.security.gaea.provider.CookieAuthenticationProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * <p>增加cookie认证策略</p>
 *
 * @author yangz
 * @version latest
 * @date 2016/1/7
 */
public class GaeaTokenAuthenticationProcessFilter extends TokenAuthenticationProcessFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AuthenticationManager authenticationManager;
    private PreAuthenticatedAuthenticationExtractorManager extractorManager;
    private AuthenticationProviderManager authenticationProviderManager;
    private GaeaTokenParser gaeaTokenParser;
    private RealmService realmService;

    public GaeaTokenAuthenticationProcessFilter(AuthenticationManager authenticationManager,
                                                PreAuthenticatedAuthenticationExtractorManager extractorManager, AuthenticationProviderManager authenticationProviderManager, GaeaTokenParser gaeaTokenParser, RealmService realmService) {
        super(authenticationManager, extractorManager);
        this.authenticationManager = authenticationManager;
        this.extractorManager = extractorManager;
        this.authenticationProviderManager = authenticationProviderManager;
        this.gaeaTokenParser = gaeaTokenParser;
        this.realmService = realmService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        logger.debug("Token authentication filter start");

        // process sdp authorization
        processAuthorization(request, response);
        // process gaea authorization
        processGaeaAuthorization(request, response);
        // init gaea context
        initGaeaContext(request, response);

//        processCookieSwap(request, response);

        logger.debug("Token authentication filter end");

        chain.doFilter(request, response);

    }

    protected String parseAuthentication(HttpServletRequest request, HttpServletResponse response) throws javax.security.sasl.AuthenticationException {
        return authenticationProviderManager.getAuthentication(request);
    }

    protected void processAuthorization(HttpServletRequest request, HttpServletResponse response) throws javax.security.sasl.AuthenticationException {
        String authorization = parseAuthentication(request, response);
        logger.debug("Authorization: {}", authorization);

        if (authorization != null) {
            long beginTime = System.currentTimeMillis();
            logger.debug("Authorization beginTime:{}", beginTime);
            try {
                Authentication authentication = extractorManager.extractAuthentication(authorization, request);
                Assert.notNull(authentication, "authentication cannot be null.");

                Authentication successAuthentication = authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(successAuthentication);
            } catch (AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                throw ex;
            } catch (WafResourceAccessException ex) {
                throw new WafAuthenticationException(SR.format("waf.er.authorized.exception", ex.getMessage()), ex, ex.getRemoteResponseEntity());
            } catch (WafException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new WafAuthenticationException(SR.format("waf.er.authorized.exception", ex.getMessage()), ex);
            } finally {
                long endTime = System.currentTimeMillis();
                logger.debug("Authorization endTime:{}, the total time:{}ms", endTime, (endTime - beginTime));
            }
        }
    }

    protected void processCookieSwap(HttpServletRequest request, HttpServletResponse response){
        Map<String, String[]> parameterNames = request.getParameterMap();
        if(parameterNames != null){
            for (Map.Entry<String, String[]> stringEntry : parameterNames.entrySet()) {
                String key = stringEntry.getKey();
                if(!StringUtils.isEmpty(key) && key.startsWith("swsc_")){
                    String newKey = key.replaceAll("swsc_", "");
                    if(CookieAuthenticationProvider.AUTHORIZATION.equals(newKey) || GaeaTokenParser.GAEA_AUTHORIZATION.equals(newKey)){
                        continue;
                    }
                    String value = request.getParameter(key);
                    Cookie cookie = new Cookie(newKey, value);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }

    protected void processGaeaAuthorization(HttpServletRequest request, HttpServletResponse response) {
        GaeaToken gaeaToken = gaeaTokenParser.parse(request);
        if (gaeaToken != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            GaeaUserAuthentication gaeaAuthentication = authentication != null && authentication instanceof WafUserAuthentication
                    ? new GaeaUserAuthentication(gaeaToken, (WafUserAuthentication) authentication)
                    : new GaeaUserAuthentication(gaeaToken);

            SecurityContextHolder.getContext().setAuthentication(gaeaAuthentication);
        }
    }

    protected void initGaeaContext(HttpServletRequest request, HttpServletResponse response) {
        GaeaContext.getLocal().put(GaeaContext.LOCAL_REQUEST, request);
        GaeaContext.getLocal().put(GaeaContext.LOCAL_RESPONSE, response);
        GaeaContext.setRealm(realmService.getRealm(request));
    }
}
