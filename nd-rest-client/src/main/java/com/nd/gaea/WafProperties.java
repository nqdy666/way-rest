package com.nd.gaea;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * 提供 WAF 使用到的配置信息的管理。可以使用 waf.properties 配置文件进行配置，或则使用 {@link WafProperties#setProperty} 进行代码配置
 *
 * @author vime
 * @since 0.9.5
 */
public class WafProperties {
    private static final Logger logger = LoggerFactory.getLogger(WafProperties.class);
    private static Properties properties;
    private static Properties defaultProperties;

    static {
        defaultProperties = new Properties();

        try {
            properties = new Properties(defaultProperties);
            InputStream stream = WafProperties.class.getClassLoader().getResourceAsStream("waf.properties");
            if (stream != null) {
                properties.load(stream);
            }
        } catch (Exception ex) {
            logger.warn("Read waf.properties error.", ex);
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static Properties getDefaultProperties() {
        return defaultProperties;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static int getPropertyForInteger(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 int 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    public static int getPropertyForInteger(String key, String defaultValue) {
        String value = getProperty(key, defaultValue);
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 int 过程发生错误，引发的 properties 属性为 " + key);
        }
    }
    
    public static boolean getPropertyForBoolean(String key, String defaultValue) {
        String value = getProperty(key, defaultValue);
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 boolean 过程发生错误，引发的 properties 属性为 " + key);
        }
    }
}
