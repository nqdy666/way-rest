package com.nd.gaea.context.i18n.support;

/**
 * @author way
 *         Created on 2016/6/12.
 */
import org.springframework.beans.factory.InitializingBean;
import com.nd.gaea.context.i18n.internal.MessageResourceResolver;

public class ResourceBundleMessageSource extends org.springframework.context.support.ResourceBundleMessageSource implements InitializingBean {
    private MessageResourceResolver resolver = new MessageResourceResolver();
    private String[] basenamePatterns = new String[0];

    public ResourceBundleMessageSource() {
    }

    public void setBasenamePattern(String basenamePattern) {
        this.setBasenamePatterns(new String[]{basenamePattern});
    }

    public void setBasenamePatterns(String[] basenamePatterns) {
        this.basenamePatterns = basenamePatterns;
        this.setBasenames(this.resolver.resolveBasenames(basenamePatterns));
    }

    public void afterPropertiesSet() throws Exception {
        if(this.basenamePatterns == null || this.basenamePatterns.length == 0) {
            this.setBasenames(this.resolver.resolveBasenames());
        }

    }
}
