package com.nd.gaea.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 项目名字:webrest
 * 类名称:Etag
 * 类描述:实现客户端缓存etag标记
 * 创建人:涂清平
 * 创建时间:2014-11-28下午4:29:41
 * 修改人:
 * 修改时间:
 * 修改备注:
 * @version
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Etag {
	boolean enabled() default false;
}
