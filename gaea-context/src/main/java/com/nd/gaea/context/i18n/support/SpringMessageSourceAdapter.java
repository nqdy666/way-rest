package com.nd.gaea.context.i18n.support;

/**
 * @author way
 *         Created on 2016/6/12.
 */
import com.nd.gaea.context.i18n.internal.AbstractMessageSource;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;

public class SpringMessageSourceAdapter extends AbstractMessageSource implements MessageSourceAware {
    private MessageSource messageSource;

    public SpringMessageSourceAdapter() {
    }

    protected String getMessageInternal(String key, Object[] args, Locale locale) {
        if(locale == null) {
            locale = Locale.getDefault();
        }

        return this.messageSource.getMessage(key, args, locale);
    }

    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource);
        this.messageSource = messageSource;
    }
}
