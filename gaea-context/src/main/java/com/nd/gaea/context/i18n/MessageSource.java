package com.nd.gaea.context.i18n;

import java.util.Locale;
import java.util.Map;

/**
 * @author way
 *         Created on 2016/6/12.
 */
public interface MessageSource {

    String getMessage(String var1);

    String getMessage(String var1, Locale var2);

    String getMessage(String var1, Object[] var2);

    String getMessage(String var1, Object[] var2, Locale var3);

    String getMessage(String var1, Map<String, Object> var2);

    String getMessage(String var1, Map<String, Object> var2, Locale var3);
}
