package com.nd.gaea;

import java.util.Locale;

/**
 * @author vime
 * @since 0.9.6
 */
public class SR {
    public static String getString(String key) {
        return I18NProvider.getString(key);
    }

    public static String getString(Locale locale, String key) {
        return I18NProvider.getString(locale, key);
    }

    public static String format(String key, Object... args) {
        return String.format(I18NProvider.getString(key), args);
    }

    public static String format(Locale locale, String key, Object... args) {
        return String.format(I18NProvider.getString(locale, key), args);
    }
}
