package com.nd.gaea.rest.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vime
 * @since 0.9.5
 */
public class PreAuthenticatedAuthenticationExtractorManager {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    private ArrayList<PreAuthenticatedAuthenticationExtractor> extractors = new ArrayList<PreAuthenticatedAuthenticationExtractor>();

    public void Append(PreAuthenticatedAuthenticationExtractor extractor) {
        extractors.add(extractor);
    }

    @Autowired
    public void appendExtractors(List<PreAuthenticatedAuthenticationExtractor> extractors)
    {
        this.extractors.addAll(extractors);
    }

    public Authentication extractAuthentication(String authentication,HttpServletRequest request) throws AuthenticationException {
        Assert.notNull(authentication, "authentication cannot be null.");

        int spaceIndex = authentication.indexOf(" ");
        if (spaceIndex > -1){
        	String prefix = authentication.substring(0, spaceIndex);
            String value = authentication.substring(spaceIndex).trim();
            
            logger.debug("Extract authentication, prefix:{}, value:{}", prefix, value);
            
            for(PreAuthenticatedAuthenticationExtractor extractor : extractors){
                if(extractor.getPrefix().equalsIgnoreCase(prefix))
                    return extractor.extractAuthentication(value, request);
            }
        }
        //throw new AuthenticationException("错误的 Authentication 格式，数据为 " + authentication);
        //throw new AuthenticationException("不支持的认证模式 " + prefix);
        return null;
    }
}
