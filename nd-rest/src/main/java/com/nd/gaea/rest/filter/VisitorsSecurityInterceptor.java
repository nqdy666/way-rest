package com.nd.gaea.rest.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.ApplicationContextUtil;
import com.nd.gaea.rest.security.services.visitor.VisitorsAdapter;
import com.nd.gaea.rest.support.WafContext;

/**
 * 游客权限验证
 * 
 * @author 110825
 * @since 0.9.6
 */
public class VisitorsSecurityInterceptor extends HandlerInterceptorAdapter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private VisitorsAdapter visitorsAdapter;
	
	private void initService(){
		ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
        if(applicationContext!= null && visitorsAdapter==null){
        	visitorsAdapter = applicationContext.getBean(VisitorsAdapter.class);
        }
	}

	@Override  
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {  
		logger.debug("preHandle interceptor start");
		String enabled = WafProperties.getProperty(WafContext.WAF_GUEST_ENABLED);
		if (!StringUtils.isEmpty(enabled)) {
			boolean flag = Boolean.parseBoolean(enabled);
			if (flag) {
				initService();
				if (visitorsAdapter!=null) {
					return visitorsAdapter.isPermit(request, response, handler);
				}
			}
		}
		logger.debug("preHandle interceptor end");
        return true;  
    }
	
}
