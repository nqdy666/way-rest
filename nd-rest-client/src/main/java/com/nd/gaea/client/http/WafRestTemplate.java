package com.nd.gaea.client.http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.nd.gaea.util.WafJsonMapper;

/**
 * @author vime
 * @since 0.9.6
 */
public class WafRestTemplate extends RestTemplate {

	private static final boolean jaxb2Present =
			ClassUtils.isPresent("javax.xml.bind.Binder", WafRestTemplate.class.getClassLoader());

	private static final boolean jackson2Present =
			ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", WafRestTemplate.class.getClassLoader()) &&
					ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", WafRestTemplate.class.getClassLoader());

	private static final boolean jacksonPresent =
			ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", WafRestTemplate.class.getClassLoader()) &&
					ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", WafRestTemplate.class.getClassLoader());

    public WafRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
        //注册转换器
        this.setMessageConverters(configureMessageConverters());
        setErrorHandler(new RestApiErrorHandler());
    }
    
    public List<HttpMessageConverter<?>> configureMessageConverters(){
    	List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
    	// 默认非 UTF-8
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
        
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<Source>());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        
        if (jaxb2Present) {
            converters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (jackson2Present) {
        	MappingJackson2HttpMessageConverter convert = new MappingJackson2HttpMessageConverter();
        	convert.setObjectMapper(WafJsonMapper.getMapper());
            converters.add(convert);
        }else if (jacksonPresent) {
        	converters.add(new org.springframework.http.converter.json.MappingJacksonHttpMessageConverter());
		}
    	return converters;
    }

    @Override
    protected <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
        return super.acceptHeaderRequestCallback(responseType);
    }

    @Override
    protected <T> RequestCallback httpEntityCallback(Object requestBody) {
        return super.httpEntityCallback(requestBody);
    }

    @Override
    protected <T> RequestCallback httpEntityCallback(Object requestBody, Type responseType) {
        return super.httpEntityCallback(requestBody, responseType);
    }

    @Override
    protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        return super.responseEntityExtractor(responseType);
    }

    protected <T> ResponseExtractor<T> httpMessageConverterExtractor(Type responseType) {
        return new HttpMessageConverterExtractor(responseType, getMessageConverters());
    }

    @Override
    protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
        return new WafClientHttpRequest(super.createRequest(url, method));
    }
}
