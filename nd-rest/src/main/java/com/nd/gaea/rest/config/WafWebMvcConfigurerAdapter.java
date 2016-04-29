/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */

package com.nd.gaea.rest.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;

import com.nd.gaea.rest.filter.ServerTraceInterceptor;
import com.nd.gaea.rest.filter.VisitorsSecurityInterceptor;
import com.nd.gaea.util.WafJsonMapper;

/**
 * 基于spring mvc 注解方式进行配置WAF的spring配置。在本类中配置如下：<br>
 * 1、rest风格的数据转换器<br>
 * 2、异常配置资源加载<br>
 * 3、默认支持的ContentType<br>
 * 4、rest风格的异常处理器{@see RestHandlerExceptionResolver}<br>
 * 5、内部注解类的扫描路径配置<br>
 *
 * @author johnny
 */
@Configuration
@ComponentScan(basePackages = {"com.nd.gaea.rest.controller"})
public class WafWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
    private static final boolean jaxb2Present =
            ClassUtils.isPresent("javax.xml.bind.Binder", WafWebMvcConfigurerAdapter.class.getClassLoader());

    private static final boolean jackson2Present =
            ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", WafWebMvcConfigurerAdapter.class.getClassLoader()) &&
                    ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", WafWebMvcConfigurerAdapter.class.getClassLoader());

    private static final boolean jacksonPresent =
            ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", WafWebMvcConfigurerAdapter.class.getClassLoader()) &&
                    ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", WafWebMvcConfigurerAdapter.class.getClassLoader());

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).favorParameter(false);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 默认非 UTF-8
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setWriteAcceptCharset(false);

        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<Source>());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        if (jaxb2Present) {
            converters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (jackson2Present) {
        	MappingJackson2HttpMessageConverter convert = new MappingJackson2HttpMessageConverter();
        	convert.setObjectMapper(WafJsonMapper.getMapper());
        	
        	//重置媒体类型不带charset
        	List<MediaType> supportedMediaTypes = new ArrayList<>();
            supportedMediaTypes.add(MediaType.APPLICATION_JSON);
            convert.setSupportedMediaTypes(supportedMediaTypes);
            
            converters.add(convert);
        } else if (jacksonPresent) {
            converters.add(new org.springframework.http.converter.json.MappingJacksonHttpMessageConverter());
        }
        customMediaTypeSupport(converters);
    }

    public void customMediaTypeSupport(List<HttpMessageConverter<?>> converters) {
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        resolver.setMaxUploadSize(5 * 1024 * 1024);
        resolver.setMaxInMemorySize(512 * 1024);
        return resolver;
    }


    /**
     * 重载 spring mvc 对异常的处理，全部异常由 {@link com.nd.gaea.rest.filter.ExceptionFilter} 接管。
     * 注意，{@link org.springframework.web.bind.annotation.ExceptionHandler @ExceptionHandler} 及 {@link org.springframework.web.bind.annotation.ResponseStatus @ResponseStatus} 将不能使用
     * @return
     */
    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new HandlerExceptionResolverComposite();
    }

    /**
     * 注册游客模式下的权限验证拦截器
     */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverTraceInterceptor());
		registry.addInterceptor(visitorsSecurityInterceptor());
	}
	
	@Bean
    public VisitorsSecurityInterceptor visitorsSecurityInterceptor() {
		return new VisitorsSecurityInterceptor();
    }
	
	@Bean
    public ServerTraceInterceptor serverTraceInterceptor() {
        return new ServerTraceInterceptor();
    }
    
}