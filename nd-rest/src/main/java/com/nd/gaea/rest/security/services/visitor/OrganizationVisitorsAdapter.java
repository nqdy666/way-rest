package com.nd.gaea.rest.security.services.visitor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import com.nd.gaea.WafException;
import com.nd.gaea.client.ApplicationContextUtil;
import com.nd.gaea.rest.security.authens.Organization;

public class OrganizationVisitorsAdapter implements VisitorsAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private VistorsService vistorsService;
	private OrganizationService organizationService;
	
	private void initService(){
		ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
        if(applicationContext!= null){
        	organizationService = applicationContext.getBean(OrganizationService.class);
        	vistorsService = applicationContext.getBean(VistorsService.class);
        }
	}
	
	@Override
	public boolean isPermit(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		
		String authorization = request.getHeader("Authorization");
		String orgName = request.getHeader("Orgname");
		logger.debug("orgName: {}", orgName);
		boolean forbidden = true;
		if (StringUtils.isEmpty(authorization) && !StringUtils.isEmpty(orgName)) {
			initService();
			if (vistorsService != null && organizationService!=null) {
				//获取当前请求的method
				String requestMethod = null;
				if (handler instanceof HandlerMethod) {
					HandlerMethod handlerMethod = (HandlerMethod)handler;
					requestMethod = handlerMethod.getBeanType().getName() +"."+ handlerMethod.getMethod().getName();
				}
				Organization organization = organizationService.query(orgName);
				forbidden = this.lookUpMapping(requestMethod, organization);
			}
		}else if(!StringUtils.isEmpty(authorization)){
			forbidden = false;
		}
		
		if (forbidden) {
			throw new WafException("WAF/GUEST_ACCESS_DENIED", "游客访问受限", HttpStatus.FORBIDDEN);
		}
		return true;
	}

	private boolean lookUpMapping(String requestMethod, Organization organization){
		boolean forbidden = true;
		if(organization!=null && requestMethod!=null){
			List<String> whiteRequestMappings = vistorsService.getWhiteRequestMappings(organization.getOrgId());
			if (whiteRequestMappings != null && whiteRequestMappings.size() > 0) {
				for (int i = 0; i < whiteRequestMappings.size(); i++) {
					if (whiteRequestMappings.get(i).equals(requestMethod)) {
						forbidden=false;
						break;
					}
				}
			}
		}
		return forbidden;
	}
}
