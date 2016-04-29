package com.nd.gaea.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 标注在类上面，表示当前对象可以转换成的其他对象
 *
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectMapping {
	String value() default "";
	
	String strategy() default "Standard";
}
