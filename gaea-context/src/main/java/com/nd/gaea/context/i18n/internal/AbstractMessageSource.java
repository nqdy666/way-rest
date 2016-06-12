package com.nd.gaea.context.i18n.internal;

/**
 * @author way
 *         Created on 2016/6/12.
 */
import com.nd.gaea.context.i18n.MessageSource;
import com.nd.gaea.core.Constants;
import com.nd.gaea.core.utils.MessageUtils;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractMessageSource implements MessageSource {
    protected static final Locale DEFAULT_LOCALE;

    public AbstractMessageSource() {
    }

    public String getMessage(String code) {
        return this.getMessage(code, this.getLocale());
    }

    public String getMessage(String key, Locale locale) {
        return this.getMessage(key, new Object[0], locale);
    }

    public String getMessage(String key, Object[] args) {
        return this.getMessage(key, args, this.getLocale());
    }

    public String getMessage(String key, Map<String, Object> namedArgs) {
        return this.getMessage(key, namedArgs, this.getLocale());
    }

    public String getMessage(String key, Object[] args, Locale locale) {
        return this.getMessageInternal(key, args, locale);
    }

    public String getMessage(String key, Map<String, Object> namedArgs, Locale locale) {
        String message = this.getMessage(key, locale);
        return MessageUtils.format(message, namedArgs);
    }

    protected abstract String getMessageInternal(String var1, Object[] var2, Locale var3);

    public Locale getLocale() {
        return DEFAULT_LOCALE;
    }

    static {
        DEFAULT_LOCALE = Constants.DEFAULT_LOCALE;
    }
}
