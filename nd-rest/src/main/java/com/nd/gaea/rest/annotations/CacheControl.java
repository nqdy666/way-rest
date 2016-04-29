package com.nd.gaea.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nd.gaea.rest.clientcache.CachePolicy;


/**
 * 
 * 项目名字:webrest
 * 类名称:CacheControl
 * 类描述:提供基于annotation Spring MVC controller中方法
 * 针对HTTP 1.1消息头中的“Cache-control”来控制的缓存注解
 * 创建人:涂清平
 * 创建时间:2014-11-28下午3:27:25
 * 修改人:
 * 修改时间:
 * 修改备注:
 * @version
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheControl {

	//缓存策略 默认为 NO_CACHE
	CachePolicy[] policy() default { CachePolicy.NO_CACHE };

	//指定了max-age
	int maxAge() default 0;

	//指定s-maxage  	
	int sMaxAge() default -1;
	

}
