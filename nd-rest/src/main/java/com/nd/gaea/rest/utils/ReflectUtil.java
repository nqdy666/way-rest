package com.nd.gaea.rest.utils;

/**
 * @author Eric
 * @ClassName: ReflectUtil
 * @Description: 通过反射获取类实例
 * @date 2015年2月28日 下午5:27:21
 */
@Deprecated
public class ReflectUtil {

    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();

    public static Object getClass(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName(className, true, loader);
        return clazz.newInstance();
    }
}
