package com.nd.gaea.context.i18n.internal;

/**
 * @author way
 *         Created on 2016/6/12.
 */
import com.nd.gaea.core.config.SystemConfig;
import com.nd.gaea.core.utils.ArrayUtils;
import com.nd.gaea.core.utils.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ObjectUtils;

public class MessageResourceResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageResourceResolver.class);
    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String JAR_SUFFIX = ".jar!/";
    private static final String TEST_CLASSES_SUFFIX = "target/test-classes/";
    private static final String CLASSES_SUFFIX = "target/classes/";
    private ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
    private String classesPath = "";

    public MessageResourceResolver() {
        try {
            Resource ex = this.resourceLoader.getResource("/");
            this.classesPath = ex.getURL().getPath();
        } catch (IOException var2) {
            LOGGER.error("Could not resolve initialize classed path!");
        }

    }

    public String[] resolveBasenames() {
        String i18nResourceStrs = SystemConfig.instance.getProperty("system.i18n.resources", "");
        if(StringUtils.isEmpty(i18nResourceStrs)) {
            i18nResourceStrs = "classpath*:/**/*-messages*.properties";
        }

        String[] arrBasenamePatterns = i18nResourceStrs.split(",");
        String[] basenames = this.resolveBasenames(arrBasenamePatterns);
        return basenames;
    }

    public String[] resolveBasenames(String[] basenamePatterns) {
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("resolve base names pattern:" + ObjectUtils.nullSafeToString(basenamePatterns));
        }

        String[] basenames = new String[0];

        for(int i = 0; i < basenamePatterns.length; ++i) {
            String[] ret = this.resolveBasename(basenamePatterns[i]);
            basenames = (String[])ArrayUtils.addAll(basenames, ret);
        }

        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("resolve base names:" + ObjectUtils.nullSafeToString(basenames));
        }

        return basenames;
    }

    protected String[] resolveBasename(String locationPattern) {
        try {
            Resource[] ex = this.resourceLoader.getResources(locationPattern);
            return this.resolveBasename(ex, this.resolvePattern(locationPattern));
        } catch (IOException var3) {
            LOGGER.error("io error", var3);
            throw new RuntimeException("Could not resolve international resource pattern [" + locationPattern + "]", var3);
        }
    }

    protected String[] resolveBasename(Resource[] resources, String locationFormat) {
        ArrayList basenameList = new ArrayList();

        for(int i = 0; i < resources.length; ++i) {
            String baseName = this.resolveResourcePath(resources[i], locationFormat);
            if(!basenameList.contains(baseName)) {
                basenameList.add(baseName);
            }
        }

        return (String[])basenameList.toArray(new String[0]);
    }

    protected String resolveResourcePath(Resource resource, String locationFormat) {
        try {
            String ex = resource.getURL().getPath();
            int pos = this.getStartIndex(ex);
            if(pos > -1) {
                ex = ex.substring(pos);
            } else {
                ex = StringUtils.replace(ex, this.classesPath, "");
            }

            return ex.substring(0, ex.indexOf(locationFormat) + locationFormat.length());
        } catch (IOException var5) {
            LOGGER.error("Could not resolve resource pattern[" + resource + "," + locationFormat + "]", var5);
            return "";
        }
    }

    private int getStartIndex(String path) {
        int pos = path.lastIndexOf(".jar!/");
        int len = ".jar!/".length();
        if(pos == -1) {
            if((pos = path.lastIndexOf("target/test-classes/")) > -1) {
                len = "target/test-classes/".length();
            } else if((pos = path.lastIndexOf("target/classes/")) > -1) {
                len = "target/classes/".length();
            }
        }

        return pos > -1?pos + len:-1;
    }

    protected String resolvePattern(String locationPattern) {
        int start = 0;
        locationPattern = locationPattern.replace("classpath*:", "");
        locationPattern = locationPattern.replaceAll("classpath:", "");
        if(locationPattern.indexOf("/") > -1) {
            start = locationPattern.lastIndexOf("/") + 1;
        }

        String simpleFormat = locationPattern.substring(start).replaceAll(".properties", "");
        simpleFormat = simpleFormat.replace('*', '=').replace('?', '=').replaceAll("=", "");
        simpleFormat = StringUtils.trim(simpleFormat);
        return simpleFormat;
    }

    public static void main(String[] args) {
        String path = "file:/D:/Workspace/Work/trunk/codes/gaea3/gaea-demo/target/gaea-demo-3.0.0-SNAPSHOT/WEB-INF/lib/gaea-core-3.0.0-SNAPSHOT.jar!/config/hello-messages.properties";
        int pos = path.lastIndexOf(".jar!/");
        System.out.println(path.substring(pos + ".jar!/".length()));
    }
}
