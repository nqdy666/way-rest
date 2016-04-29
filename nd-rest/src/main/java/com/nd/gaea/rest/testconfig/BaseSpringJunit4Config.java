package com.nd.gaea.rest.testconfig;

import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.nd.gaea.client.http.WafSecurityHttpClient;
import com.nd.gaea.rest.exceptions.WafErrorResolver;
import com.nd.gaea.rest.filter.ExceptionFilter;
import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import com.nd.gaea.rest.security.services.impl.UserCenterUserDetailsService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public abstract class BaseSpringJunit4Config {

	protected MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
    private List<WafErrorResolver> resolvers;
	
	private static WafUserAuthentication authentication;
	
	protected String userId;
	
	protected String realm;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	@Before
	public void setUp(){	
		ExceptionFilter exceptionFilter = new ExceptionFilter();
    	exceptionFilter.setWafErrorResolvers(resolvers);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(exceptionFilter).build();
		if (authentication==null) {
			authentication = this.getPrincipal(this.getUserId(), this.getRealm());
			/**
			 * 把用户信息添加到全局的SecurityContextHolder对象中。
			 */
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}


	protected WafUserAuthentication getPrincipal(String userId, String realm) {
		
		/**
		 * 连接uc，获得用户信息
		 */
		UserCenterUserDetailsService userCenterUserDetailsService = new UserCenterUserDetailsService();
		userCenterUserDetailsService.setWafSecurityHttpClient(new WafSecurityHttpClient());
		WafUserAuthentication authentication = new WafUserAuthentication((List)null);
		UserCenterUserDetails userCenterUserDetails = userCenterUserDetailsService
				.loadUserDetailsByUserIdAndRealm(userId, realm);
		authentication.setDetails(userCenterUserDetails);
		return authentication;
	}
}
