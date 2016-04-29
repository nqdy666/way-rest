package com.nd.gaea.rest.security.services.impl;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.http.WafSecurityHttpClient;
import com.nd.gaea.client.support.WafContext;
import com.nd.gaea.rest.security.authens.UserCenterRoleDetails;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: linzh
 * Date: 15-6-25
 * Time: 下午5:17
 * To change this template use File | Settings | File Templates.
 */
public class UserCenterUserDetailsServiceTest {
    private UserCenterUserDetailsService service = new UserCenterUserDetailsService();

    private WafSecurityHttpClient httpClient = mock(WafSecurityHttpClient.class);

    @Before
    public void setUp()
    {

    }

    @Test
    public void testGetUserRoleList()
    {
        UserCenterUserDetailsService.UserCenterRoleDetailsResponse detailsResponse = new UserCenterUserDetailsService.UserCenterRoleDetailsResponse();
        List<UserCenterRoleDetails> roleDetailses = new ArrayList<UserCenterRoleDetails>();
        detailsResponse.setItems(roleDetailses);

        UserCenterRoleDetails roleDetails = new UserCenterRoleDetails();
        roleDetails.setRealm("realm");
        roleDetails.setRoleName("role name");
        roleDetails.setRoleId("role id");
        roleDetails.setUpdatedAt(new Date());
        roleDetailses.add(roleDetails);



        given(httpClient.getForObject(any(String.class), any(Class.class), any(String.class), any(String.class))).willReturn(detailsResponse);

        WafProperties.setProperty(WafContext.WAF_UC_URI, "http://www.baidu.com");
        WafProperties.setProperty("waf.uc.get.userRoles", "users/871101/roles?realm=realm");

        service.setWafSecurityHttpClient(httpClient);

        String uid = "871101";
        String realm = "realm";
        List<UserCenterRoleDetails> roles = service.getUserRoleList(uid, realm);
        // System.out.println(uid);
        assertEquals("获取role id正常", "role id", roles.get(0).getRoleId());
        assertEquals("获取realm", "realm", roles.get(0).getRealm());
    }

    public void testGetUserRolesUrl()
    {
        WafProperties.setProperty(WafContext.WAF_UC_URI, "http://www.baidu.com");
        WafProperties.setProperty("waf.uc.get.userRoles", "users/871101/roles?realm=realm");
        String url = service.getUserRolesUrl();
        System.out.println(url);
    }
}
