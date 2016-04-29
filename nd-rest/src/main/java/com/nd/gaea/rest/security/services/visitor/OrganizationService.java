package com.nd.gaea.rest.security.services.visitor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.http.WafSecurityHttpClient;
import com.nd.gaea.rest.security.authens.Organization;
import com.nd.gaea.rest.support.WafContext;
import com.nd.gaea.util.UrlUtil;

/**
 * 组织服务类
 * @author 110825
 * @since 0.9.6
 */
public class OrganizationService {
	
	public static final String UC_FRAGMENT_ORGANIZATIONS_ACTIONS_QUERY = "waf.uc.organizations.actions.query";
	
	@Autowired
    private WafSecurityHttpClient httpClient;

    private String getOrganizationsQueryUrl() {
        return UrlUtil.combine(
                WafProperties.getProperty(WafContext.WAF_UC_URI),
                WafProperties.getProperty(UC_FRAGMENT_ORGANIZATIONS_ACTIONS_QUERY, "organizations/actions/query")
        );
    }
	
	public Organization query(String orgName){
        Assert.notNull(orgName, "orgName");
        String organizationsQueryUrl = this.getOrganizationsQueryUrl();
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("org_name", orgName);
        return httpClient.postForObject(organizationsQueryUrl, requestBody, Organization.class);
	}
}
