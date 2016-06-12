package com.nd.gaea.context.i18n;

import com.nd.gaea.context.i18n.MessageSource;
import com.nd.gaea.context.i18n.internal.MessageResourceResolver;
import com.nd.gaea.context.i18n.support.ResourceBundleMessageSource;
import com.nd.gaea.context.i18n.support.SpringMessageSourceAdapter;
import com.nd.gaea.core.utils.MessageUtils;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author way
 *         Created on 2016/6/12.
 */
public abstract class MessageProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProvider.class);
    static MessageSource messageSource;

    public MessageProvider() {
    }

    public static String getMessage(String key, Object... args) {
        return getMessage(key, getDefaultLocale(), args);
    }

    public static String getMessage(String key, Map<String, Object> namedArgs) {
        return getMessage(key, getDefaultLocale(), namedArgs);
    }

    public static String getMessage(String key, Locale locale, Map<String, Object> namedArgs) {
        String message = getMessage(key, locale, new Object[0]);
        return MessageUtils.format(message, namedArgs);
    }

    public static String getMessage(String key, Locale locale, Object... args) {
        Assert.hasText(key);
        if(messageSource != null) {
            try {
                return messageSource.getMessage(key, args, locale);
            } catch (Exception var4) {
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Error while get message [%s] with locale [%s]", new Object[]{key, locale}), var4);
                }
            }
        }

        return key;
    }

    public static boolean hasKey(String key, Locale locale) {
        Assert.hasText(key);
        if(messageSource != null) {
            try {
                messageSource.getMessage(key, new Object[0], locale);
                return true;
            } catch (Exception var3) {
                return false;
            }
        } else {
            return false;
        }
    }

    private static Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    static {
        SpringMessageSourceAdapter adapter = new SpringMessageSourceAdapter();
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        MessageResourceResolver resolver = new MessageResourceResolver();
        String[] basenames = resolver.resolveBasenames();
        source.setBasenames(basenames);
        adapter.setMessageSource(source);
        messageSource = adapter;
    }
}
