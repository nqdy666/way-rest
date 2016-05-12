package com.nd.gaea.waf.config;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by vime on 2016/1/8.
 */
public class ArrayHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        if(methodParameter.hasParameterAnnotation(RequestParam.class)) {
            if(parameterType.isArray())
                return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        RequestParam ann = methodParameter.getParameterAnnotation(RequestParam.class);
        String name = ann != null ? ann.value(): methodParameter.getParameterName();

        String[] paramValues = nativeWebRequest.getParameterValues(name);
        if(paramValues.length == 1)
            paramValues = paramValues[0].split(",");
        if(paramValues.length == 1)
            return paramValues[0];
        return paramValues;
    }
}
