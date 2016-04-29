package com.nd.gaea.rest.security.authentication.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationExtractor;
import com.nd.gaea.rest.security.services.RealmService;
import com.nd.gaea.rest.support.WafContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author vime
 * @since 0.9.5
 */
@Component
@Order(30)
public class PreAuthenticatedDebugTokenAuthenticationExtractor extends AbstractPreAuthenticatedAuthenticationExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private RealmService realmService;

    @Autowired
    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    @Override
    public String getPrefix() {
        return "DEBUG";
    }

    @Override
    public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request) throws AuthenticationException {
        if (!WafContext.isDebugMode())
            throw new RuntimeException("当前非调试模式，无法使用。请使用配置 " + WafContext.WAF_DEBUG_ENABLED + "=true");

        String debugToken = authenticationValue;
        Assert.hasText(debugToken, "Debug token should contains text.");

        Map<String, String> map = splitToMap(debugToken);
        String userId = getValue(map, "userid");
        Assert.hasText(userId, "Debug token property userid is missing.");

        String realm = getValue(map, "realm");
        if (!StringUtils.hasText(realm))
            realm = realmService.getRealm(request);
        Assert.notNull(realm, "realm connot be null.");

        return new PreAuthenticatedDebugTokenAuthentication(userId, realm);
    }
}
