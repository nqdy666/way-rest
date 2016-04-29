package com.nd.gaea.rest.security.authentication.mac;

import java.util.Map;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.nd.gaea.rest.security.authentication.AbstractPreAuthenticatedAuthenticationExtractor;
import com.nd.gaea.rest.security.services.RealmService;

/**
 * @author vime
 * @since 0.9.5
 */
@Component
@Order(20)
public class PreAuthenticatedMacTokenAuthenticationExtractor extends
        AbstractPreAuthenticatedAuthenticationExtractor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RealmService realmService;

    @Autowired
    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    @Override
    public String getPrefix() {
        return "MAC";
    }

    @Override
    public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request) throws AuthenticationException {
        String macToken = authenticationValue;
        Assert.hasText(macToken, "Mac token should contains text.");

        String host = request.getHeader(HttpHeaders.HOST);
        Assert.hasText(host, "请求头部信息 host 不能为空");

        String requestURI = this.getRequestURL(request);
        requestURI = this.getURI(host, requestURI);
		
		logger.debug("requestURI:{}, host:{}", requestURI, host);

        Map<String, String> map = splitToMap(macToken);

        String id = getValue(map, "id");
        String nonce = getValue(map, "nonce");
        String mac = getValue(map, "mac");

        Assert.hasText(id, "Mac token property id is missing.");
        Assert.hasText(nonce, "Mac token property nonce is missing.");
        Assert.hasText(mac, "Mac token property mac is missing.");

        logger.debug("mac:{}, id:{}, nonce:{}", mac, id, nonce);
        String realm = realmService.getRealm(request);
        return new PreAuthenticatedMacTokenAuthentication(id, mac, nonce, request.getMethod(), requestURI, host, realm);
    }
    
	private String getRequestURL(HttpServletRequest request) {
		String reqString = request.getRequestURL().toString();
		String queryStr = request.getQueryString();
		// 判断请求参数是否为空
		if (!StringUtils.isEmpty(queryStr)) {
			reqString = reqString + "?" + queryStr;
		}
		return reqString;
	}

    private String getURI(String host, String url) {
        //需要考虑使用ip地址的时候端口号的问题
        if (host == "") {
            return "";
        }

        int index = url.indexOf(host);
        if (index == -1) {
            return "";
        }

        return url.substring(index + host.length());
    }

}
