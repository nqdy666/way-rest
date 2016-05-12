package com.nd.gaea.waf.security.gaea;

import com.nd.gaea.rest.security.authens.UserCenterUserDetails;
import com.nd.gaea.rest.security.authens.WafUserAuthentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;

/**
 * Created by vime on 2016/2/19.
 */
public class GaeaUserAuthentication extends WafUserAuthentication {
    private GaeaToken gaeaToken;

    public GaeaUserAuthentication(GaeaToken gaeaToken, WafUserAuthentication wafUserAuthentication) {
        super((UserCenterUserDetails) wafUserAuthentication.getDetails(), wafUserAuthentication.getToken());
        this.gaeaToken = gaeaToken;
    }

    public GaeaUserAuthentication(GaeaToken gaeaToken) {
        super(new ArrayList<GrantedAuthority>());
        this.gaeaToken = gaeaToken;
    }

    public GaeaToken getGaeaToken() {
        return gaeaToken;
    }
}
