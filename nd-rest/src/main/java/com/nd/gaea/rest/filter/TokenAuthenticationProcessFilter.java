/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.nd.gaea.SR;
import com.nd.gaea.WafException;
import com.nd.gaea.client.ApplicationContextUtil;
import com.nd.gaea.client.WafResourceAccessException;
import com.nd.gaea.rest.security.authentication.PreAuthenticatedAuthenticationExtractorManager;
import com.nd.gaea.rest.security.authentication.WafAuthenticationException;
import com.nd.gaea.rest.security.authentication.mac.PreAuthenticatedMacTokenAuthentication;
import com.nd.gaea.rest.security.services.impl.MacTokenService;

/**
 * 自定义的Mactoken的认证处理Filter对象。其扩展自{@link GenericFilterBean}抽象类。<br>
 * 1、配置 AuthenticationEntryPoint <br>
 * 2、获取请求Authoritation头信息，并且解析 <br>
 * 3、通过Provider进行认证处理<br>
 * 4、认证后获取用户信息<br>
 *
 * @author johnny
 */
public class TokenAuthenticationProcessFilter extends GenericFilterBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AuthenticationManager authenticationManager;

    private PreAuthenticatedAuthenticationExtractorManager extractorManager;
    
    private MacTokenService macTokenService;
    public void setMacTokenService(MacTokenService macTokenService) {
		this.macTokenService = macTokenService;
	}
    
    private void initService(){
		ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
        if(applicationContext!= null){
        	if (macTokenService==null) {
        		macTokenService = applicationContext.getBean(MacTokenService.class);
			}
        }
	}
    
	public TokenAuthenticationProcessFilter(
            AuthenticationManager authenticationManager, PreAuthenticatedAuthenticationExtractorManager extractorManager) {
        this.authenticationManager = authenticationManager;
        this.extractorManager = extractorManager;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        
		logger.debug("Token authentication filter start");
		
		String authorization = request.getHeader("Authorization");
		logger.debug("Authorization: {}", authorization);
			
		if (authorization != null) {
            long beginTime = System.currentTimeMillis();
            logger.debug("Authorization beginTime:{}", beginTime);

            try {
                Authentication authentication = extractorManager.extractAuthentication(authorization, request);
				if (authentication != null) {
					boolean disableMacAuthentication = false;
					if (authentication instanceof PreAuthenticatedMacTokenAuthentication) {
						initService();
					 	/**
					 	 * 判断是否跳过mac认证
					 	 */
						if (macTokenService != null && macTokenService.disableMacTokenAuthentication(request)) {
							disableMacAuthentication = true;
						}
					}
					if (!disableMacAuthentication) {
						Authentication successAuthentication = authenticationManager.authenticate(authentication);
						SecurityContextHolder.getContext().setAuthentication(successAuthentication);
					}
				}
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
		chain.doFilter(request, response);
		logger.debug("Token authentication filter end");
    }
}
