package com.nd.gaea.waf.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nd.gaea.util.WafJsonMapper;

import java.util.Date;

/**
 * Created by vime on 2016/2/22.
 */
public class GaeaJsonMapper extends WafJsonMapper {
    public static Date parseDate(String value) {
        return getMapper().convertValue(value, Date.class);
    }

    public static String convertString(Object value) {
        try {
            return getMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
