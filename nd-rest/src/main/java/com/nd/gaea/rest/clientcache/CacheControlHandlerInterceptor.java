package com.nd.gaea.rest.clientcache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.nd.gaea.rest.annotations.CacheControl;
import com.nd.gaea.rest.annotations.Etag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;
/**
 * 
 * 项目名字:webrest
 * 类名称:CacheControlHandlerInterceptor
 * 类描述:缓存拦截器
 * 创建人:涂清平
 * 创建时间:2014-11-28下午3:35:38
 * 修改人:
 * 修改时间:
 * 修改备注:
 * @version
 */
public class CacheControlHandlerInterceptor extends HandlerInterceptorAdapter implements HandlerInterceptor {

	private static final String HEADER_EXPIRES = "Expires";
	
	//HTTP消息头中Cache-control
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";
	
	private static String HEADER_ETAG = "ETag";
	private static String HEADER_IF_NONE_MATCH = "If-None-Match";
	
	private boolean useExpiresHeader = true;
	
	private final static Logger logger = LoggerFactory.getLogger(CacheControlHandlerInterceptor.class);
		
	/**
	 * 创建新拦截器
	 */
	public CacheControlHandlerInterceptor() {
		super();
	}
	
	/**
	 * 分配缓存
	 * @param request
	 * @param response
	 * @param handler
	 * @throws IOException 
	 */
	protected final void assignCacheControlHeader(final HttpServletRequest request,final HttpServletResponse response, final Object handler) throws IOException {
		
		final CacheControl cacheControl = this.getCacheControl(request, response, handler);
		final String cacheControlHeader = this.createCacheControlHeader(cacheControl);
		
		if (cacheControlHeader != null) {
			response.setHeader(HEADER_CACHE_CONTROL, cacheControlHeader);
			if (useExpiresHeader) {
				response.setDateHeader(HEADER_EXPIRES, createExpiresHeader(cacheControl));
			}
		}
		
		if(enabledEtag(request, response, handler)){		
			updateResponse(request, new ShallowEtagResponseWrapper(response));		
		}
	}
	
	
	protected final String createCacheControlHeader(final CacheControl cacheControl) {

		final StringBuilder builder = new StringBuilder();

		if (cacheControl == null) {
			return null;
		}

		final CachePolicy[] policies = cacheControl.policy();

		if (cacheControl.maxAge() >= 0) {
			builder.append("max-age=").append(cacheControl.maxAge());
		}

		if (cacheControl.sMaxAge() >= 0) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("s-maxage=").append(cacheControl.sMaxAge());
		}

		if (policies != null) {
			for (final CachePolicy policy : policies) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(policy.policy());
			}
		}

		return (builder.length() > 0 ? builder.toString() : null);
	}
	
	
	protected final long createExpiresHeader(final CacheControl cacheControl) {
		
		final Calendar expires = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		
		if (cacheControl.maxAge() >= 0) {
			expires.add(Calendar.SECOND, cacheControl.maxAge());
		}
		
		return expires.getTime().getTime();
	}
	
	
	public final CacheControl getCacheControl(final HttpServletRequest request,final HttpServletResponse response, final Object handler) {
		
		if (handler == null || !(handler instanceof HandlerMethod)) {
			return null;
		}
		
		final HandlerMethod handlerMethod = (HandlerMethod) handler;
		CacheControl cacheControl = handlerMethod.getMethodAnnotation(CacheControl.class);
		
		if (cacheControl == null) {
			return handlerMethod.getBeanType().getAnnotation(CacheControl.class);
		}
		
		return cacheControl;
	}
	
	@Override
	public final boolean preHandle(final HttpServletRequest request,final HttpServletResponse response, final Object handler) throws Exception {
		
		this.assignCacheControlHeader(request, response, handler);
		
		return super.preHandle(request, response, handler);
	}
	
	public final void setUseExpiresHeader(final boolean useExpiresHeader) {
		this.useExpiresHeader = useExpiresHeader;
	}
	
	
	private void updateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ShallowEtagResponseWrapper  responseWrapper = WebUtils.getNativeResponse(response, ShallowEtagResponseWrapper.class);
		Assert.notNull(responseWrapper, "ShallowEtagResponseWrapper not found");

		response = (HttpServletResponse) responseWrapper.getResponse();

		byte[] body = responseWrapper.toByteArray();
		int statusCode = responseWrapper.getStatusCode();

		if (isEligibleForEtag(request, responseWrapper, statusCode, body)) {
			String responseETag = generateETagHeaderValue(body);
			response.setHeader(HEADER_ETAG, responseETag);

			String requestETag = request.getHeader(HEADER_IF_NONE_MATCH);
			if (responseETag.equals(requestETag)) {
				if (logger.isInfoEnabled()) {
					logger.info("ETag [{}] equal to If-None-Match, sending 304", responseETag);
				}
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			}
			else {
				
				logger.info("ETag [{}] not equal to If-None-Match [{}], sending normal response", responseETag, requestETag);
				
				copyBodyToResponse(body, response);
			}
		}
		else {
			
			logger.info("Response with status code [{}] not eligible for ETag", statusCode);
			
			copyBodyToResponse(body, response);
		}
	}

	private void copyBodyToResponse(byte[] body, HttpServletResponse response) throws IOException {
		if (body.length > 0) {
			response.setContentLength(body.length);
			FileCopyUtils.copy(body, response.getOutputStream());
		}
	}
	
	protected boolean isEligibleForEtag(HttpServletRequest request, HttpServletResponse response,
			int responseStatusCode, byte[] responseBody) {

		return (responseStatusCode >= 200 && responseStatusCode < 300);
	}
	
	protected String generateETagHeaderValue(byte[] bytes) {
		StringBuilder builder = new StringBuilder("\"0");
		DigestUtils.appendMd5DigestAsHex(bytes, builder);
		builder.append('"');
		return builder.toString();
	}
	
	
	protected final boolean enabledEtag(
			final HttpServletRequest request,
			final HttpServletResponse response, 
			final Object handler) {
		
		if (handler == null || !(handler instanceof HandlerMethod)) {
			return false;
		}
		
		final HandlerMethod handlerMethod = (HandlerMethod) handler;
		Etag etag = handlerMethod.getMethodAnnotation(Etag.class);
		
		if (etag == null) {
			if(handlerMethod.getBeanType().isAnnotation())
				return handlerMethod.getBeanType().getAnnotation(Etag.class).enabled();
			else 
				return false;
		}
		
		return etag.enabled();
	}
	
	
	private static class ShallowEtagResponseWrapper extends HttpServletResponseWrapper {

		private final ByteArrayOutputStream content = new ByteArrayOutputStream();

		private final ServletOutputStream outputStream = new ResponseServletOutputStream();

		private PrintWriter writer;

		private int statusCode = HttpServletResponse.SC_OK;

		private ShallowEtagResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void setStatus(int sc) {
			super.setStatus(sc);
			this.statusCode = sc;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void setStatus(int sc, String sm) {
			super.setStatus(sc, sm);
			this.statusCode = sc;
		}

		@Override
		public void sendError(int sc) throws IOException {
			super.sendError(sc);
			this.statusCode = sc;
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			super.sendError(sc, msg);
			this.statusCode = sc;
		}

		@Override
		public void setContentLength(int len) {
		}

		@Override
		public ServletOutputStream getOutputStream() {
			return this.outputStream;
		}

/*		@Override
		public PrintWriter getWriter() throws IOException {
			if (this.writer == null) {
				String characterEncoding = getCharacterEncoding();
				this.writer = (characterEncoding != null ? new ResponsePrintWriter(characterEncoding) :
						new ResponsePrintWriter(WebUtils.DEFAULT_CHARACTER_ENCODING));
			}
			return this.writer;
		}*/

		@Override
		public void resetBuffer() {
			this.content.reset();
		}

		@Override
		public void reset() {
			super.reset();
			resetBuffer();
		}

		private int getStatusCode() {
			return statusCode;
		}

		private byte[] toByteArray() {
			return this.content.toByteArray();
		}

		private class ResponseServletOutputStream extends ServletOutputStream {

			@Override
			public void write(int b) throws IOException {
				content.write(b);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				content.write(b, off, len);
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setWriteListener(WriteListener arg0) {
			}
		}

		private class ResponsePrintWriter extends PrintWriter {

			private ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
				super(new OutputStreamWriter(content, characterEncoding));
			}

			@Override
			public void write(char buf[], int off, int len) {
				super.write(buf, off, len);
				super.flush();
			}

			@Override
			public void write(String s, int off, int len) {
				super.write(s, off, len);
				super.flush();
			}

			@Override
			public void write(int c) {
				super.write(c);
				super.flush();
			}
		}
	}
}

