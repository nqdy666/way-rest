package com.nd.gaea.rest.security.authentication;

import com.google.common.base.Splitter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author vime
 * @since 0.9.6
 */
public abstract class AbstractPreAuthenticatedAuthenticationExtractor implements PreAuthenticatedAuthenticationExtractor {

    protected Map<String, String> splitToMap(String data) {
        return new CaseInsensitiveMap(Splitter.on(",").trimResults().withKeyValueSeparator(Splitter.on("=").trimResults().limit(2)).split(data));
    }

    protected String getValue(Map<String, String> map, String name) {
        String value = map.get(name);
        if (value != null)
            value = StringUtils.strip(value, "\"");
        return value;
    }
}
