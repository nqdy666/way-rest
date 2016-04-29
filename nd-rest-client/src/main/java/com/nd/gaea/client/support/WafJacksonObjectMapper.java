/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.client.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * 自定义的Jackson对象和java对象的映射类。其中配置的数据转换的策略内容
 * 主要设置java驼峰命名结构和json的下划线结构的处理
 * @deprecated 请使用 {@link com.nd.gaea.util.WafJsonMapper}
 * @author Administrator
 * @since 0.9.5
 */
@Deprecated
public class WafJacksonObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = -5997596521773820601L;

    public WafJacksonObjectMapper() {
        setOptions();
    }

    private void setOptions() {

        this.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);    // 设置将驼峰命名法转换成下划线的方式输入输出

        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  // 设置时间为 ISO-8601 日期
        this.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        this.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        this.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, false);

        // 如果输入不存在的字段时不会报错
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        // 使用默认的Jsckson注解
        this.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        /*
         * 设定是否使用Enum的toString函数来读取Enum, 为False时使用Enum的name()函数来读取Enum,
		 * 默认为False.
		 */


        // 是否缩放排列输出，默认false，有些场合为了便于排版阅读则需要对输出做缩放排列
        // this.configure(SerializationFeature.INDENT_OUTPUT, true);
        // 是否环绕根元素，默认false，如果为true，则默认以类名作为根元素
        // this.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        // 序列化BigDecimal时之间输出原始数字还是科学计数，默认false，即是否以toPlainString()科学计数方式来输出
        this.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, false);
    }
}
