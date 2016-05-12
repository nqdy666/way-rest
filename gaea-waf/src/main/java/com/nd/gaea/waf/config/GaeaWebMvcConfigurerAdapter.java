package com.nd.gaea.waf.config;

import com.nd.gaea.rest.config.WafWebMvcConfigurerAdapter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>WAF 增加支持 application/*+json 模式</p>
 *
 * @author  yangz
 * @date    2016/1/6
 * @version latest
 */
public class GaeaWebMvcConfigurerAdapter extends WafWebMvcConfigurerAdapter {

    @Override
    public void customMediaTypeSupport(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter converter: converters){
            if(converter.getClass().isAssignableFrom(MappingJackson2HttpMessageConverter.class)){
                MappingJackson2HttpMessageConverter mappingJacksonConverter = (MappingJackson2HttpMessageConverter)converter;
                List<MediaType> supportedMediaTypes = new ArrayList<>();
                supportedMediaTypes.add(MediaType.APPLICATION_JSON);
                supportedMediaTypes.add(new MediaType("application", "*+json"));
                mappingJacksonConverter.setSupportedMediaTypes(supportedMediaTypes);
            }
        }
    }

    //        @Override
//        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//            argumentResolvers.add(new ArrayHandlerMethodArgumentResolver());
//        }

}
