package com.nd.gaea.rest.clientcache;

import javax.servlet.http.HttpServletRequest;

//import nd.com.gaer.rest.annotations.LastModifiedCache;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.LastModified;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;


/**
 * 
 * 项目名字:webrest
 * 类名称:NdRequestMappingHandlerAdapter
 * 类描述:
 * 创建人:涂清平
 * 创建时间:2014-12-1上午11:24:48
 * 修改人:
 * 修改时间:
 * 修改备注:
 * @version
 */
public class LastModifiedRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter   {
	
	@Override
	protected long getLastModifiedInternal(HttpServletRequest request, HandlerMethod handlerMethod) {
		if (handlerMethod instanceof LastModified) {
			return ((LastModified) handlerMethod).getLastModified(request);
		}		
		if(enabledLastModifiedCache(request, handlerMethod)){
			return System.currentTimeMillis();
		}
		return -1L;
	}
	
	
	protected final boolean enabledLastModifiedCache(
			final HttpServletRequest request, 
			final Object handler) {
		
		if (handler == null || !(handler instanceof HandlerMethod)) {
			return false;
		}
		
		final HandlerMethod handlerMethod = (HandlerMethod) handler;
//		LastModifiedCache lastModified = handlerMethod.getMethodAnnotation(LastModifiedCache.class);
//		
//		if (lastModified == null) {
//			if(handlerMethod.getBeanType().isAnnotation())
//				return handlerMethod.getBeanType().getAnnotation(LastModifiedCache.class).enabled();
//			else 
//				return false;
//		}
//		
//		return lastModified.enabled();
		
		return true;
	}

}
