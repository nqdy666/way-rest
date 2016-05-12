package com.nd.gaea.waf.i18n;

import com.nd.gaea.I18NProvider;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * <p>国际化依赖HTTP Header Access-Language</p>
 *
 * @author  yangz
 * @date    2016/1/11
 * @version latest
 */
public class GaeaI18NProvider extends I18NProvider{

    @Override
    protected Locale getDefaultLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale;
    }

}
