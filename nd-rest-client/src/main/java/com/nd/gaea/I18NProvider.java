package com.nd.gaea;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

import sun.util.ResourceBundleEnumeration;

/**
 * @author vime
 * @since 0.9.6
 */
public class I18NProvider {

    private static I18NProvider provider;

    static {
        provider = new I18NProvider();
    }

    public static void setProvider(I18NProvider provider) {
        I18NProvider.provider = provider;
    }

    public static I18NProvider getProvider() {
        return provider;
    }

    public static String getString(String name) {
        Assert.hasText(name);
        if (name.startsWith("resource:"))
            name = name.substring(9).trim();
        ResourceBundle resourceBundle = provider.getResourceBundle();
        if (resourceBundle.containsKey(name))
            return resourceBundle.getString(name);
        return name;
    }

    public static String getString(Locale locale, String name) {
        Assert.hasText(name);
        Assert.notNull(locale);
        
        if (name.startsWith("resource:"))
            name = name.substring(9).trim();
        ResourceBundle resourceBundle = provider.getResourceBundle(locale);
        if (resourceBundle.containsKey(name))
            return resourceBundle.getString(name);
        return name;
    }

    public static boolean containsKey(String name) {
        return provider.getResourceBundle().containsKey(name);
    }

    public static boolean containsKey(Locale locale, String name) {
        return provider.getResourceBundle(locale).containsKey(name);
    }

    public ResourceBundle getResourceBundle() {
        Locale locale = getDefaultLocale();
        MultiControl multiControl = new MultiControl();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("waf_resource", locale, multiControl);
        return resourceBundle;
    }

    public ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle("waf_resource", locale, new MultiControl());
    }

    protected Locale getDefaultLocale() {
        return LocaleContextHolder.getLocale();
    }

    private static class MultiControl extends ResourceBundle.Control {
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");

            List<URL> resources = Collections.list(loader.getResources(resourceName));
            Collections.reverse(resources);
            return new MultiResourcePropertyResourceBundle(resources);
        }
    }

    private static class MultiResourcePropertyResourceBundle extends ResourceBundle {
        private HashMap lookup;

        public MultiResourcePropertyResourceBundle(List<URL> urls) throws IOException {
            lookup = new HashMap();
            for (URL url : urls) {
                URLConnection urlc = url.openConnection();
                InputStream is = urlc.getInputStream();
                try {
                    Properties temp = new Properties();
                    temp.load(is);
                    lookup.putAll(temp);
                } finally {
                    is.close();
                }
            }
        }

        @Override
        protected Object handleGetObject(String key) {
            if (key == null) {
                throw new NullPointerException();
            }
            return lookup.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            ResourceBundle parent = this.parent;
            return new ResourceBundleEnumeration(lookup.keySet(),
                    (parent != null) ? parent.getKeys() : null);
        }

        @Override
        protected Set<String> handleKeySet() {
            return lookup.keySet();
        }
    }

}
