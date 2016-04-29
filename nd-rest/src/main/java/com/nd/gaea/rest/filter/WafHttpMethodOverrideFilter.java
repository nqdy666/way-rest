package com.nd.gaea.rest.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.nd.gaea.util.WafJsonMapper;

/**
 * ie8/ie9跨域支持过滤器
 */
public class WafHttpMethodOverrideFilter extends OncePerRequestFilter {
    public static final String PROXY_PARAM = "$proxy";
    public static final String PROXY_PARAM_ENCODE = "%24proxy";
    public static final String METHOD_PARAM = "$method";
    public static final String BODY_PARAM = "$body";
    public static final String HEADERS_PARAM = "$headers";
    public static final String STATUS = "$status";
    public static final String STATUS_TEXT = "$status_text";

    private static final String defaultCharset = "UTF-8";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("Http override filter start");
        String proxyValue = request.getParameter(PROXY_PARAM);
        if ("POST".equals(request.getMethod()) && "body".equals(proxyValue)) {

            ObjectMapper mapper = WafJsonMapper.getMapper();
            JsonNode proxyNode = mapper.readTree(request.getInputStream());
            JsonNode methodNode = proxyNode.get(METHOD_PARAM);
            Assert.notNull(methodNode, "属性 " + METHOD_PARAM + " 不能为空");

            JsonNode headersNode = proxyNode.get(HEADERS_PARAM);
            if (headersNode == null)
                headersNode = NullNode.getInstance();
            JsonNode bodyNode = proxyNode.get(BODY_PARAM);
            String bodyStr = null;
            if (bodyNode!=null) {
            	bodyStr = bodyNode.asText();
            	if (StringUtils.isEmpty(bodyStr)) {
            		bodyStr = bodyNode.toString();
				}
			}
            
            HttpServletRequest httpRequestWrapper = new HttpMethodRequestWrapper(request, methodNode.asText(), headersNode, bodyStr);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            HttpServletResponseWrapper httpResponseWrapper = new HttpServletResponseWrapper(response, os);

            filterChain.doFilter(httpRequestWrapper, httpResponseWrapper);
            //刷新response的输出流, 在某些情况下, 如果不刷新的话, 会导致部分信息还在缓存中, 而没有写入os中
            handleResponse(os, httpResponseWrapper, response, headersNode);

        } else {
            filterChain.doFilter(request, response);
        }
		logger.debug("Http override filter end");
    }

    private void handleResponse(ByteArrayOutputStream os, HttpServletResponseWrapper httpResponseWrapper, HttpServletResponse response, JsonNode headersObject) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(HEADERS_PARAM, headersObject);

		map.put(STATUS, httpResponseWrapper.getStatus());
		map.put(STATUS_TEXT, "OK");

		String body = new String(os.toByteArray(), defaultCharset);
		if (!StringUtils.isEmpty(body)) {
			map.put(BODY_PARAM, body);
		}
		
		response.setCharacterEncoding(defaultCharset);
		response.setContentLength(-1);
		PrintWriter out = response.getWriter();
		ObjectMapper mapper = WafJsonMapper.getMapper();
		mapper.writeValue(out, map);
    }

    private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

        private final HttpServletRequest request;
        private final String method;
        private final JsonNode headersNode;
        private String body;

        public HttpMethodRequestWrapper(HttpServletRequest request, String method, JsonNode headersNode, String bodyNode) {
            super(request);
            this.request = request;
            this.method = method.toUpperCase();
            if (headersNode == null)
                headersNode = NullNode.getInstance();
            this.headersNode = headersNode;
            if (bodyNode != null)
                this.body = bodyNode;
        }

        private String tryGetString(JsonNode node, String name) {
            JsonNode jsonNode = node.get(name);
            if (jsonNode != null)
                return jsonNode.asText();
            return null;
        }

        private long tryGetLong(JsonNode node, String name) {
            JsonNode jsonNode = node.get(name);
            if (jsonNode != null)
                return jsonNode.asLong();
            return 0;
        }

        private int tryGetInt(JsonNode node, String name) {
            JsonNode jsonNode = node.get(name);
            if (jsonNode != null)
                return jsonNode.asInt();
            return 0;
        }

		@Override
		public String getQueryString() {
			StringBuffer reqQueryStr = new StringBuffer();
			String queryStr = request.getQueryString();
			// 判断请求参数是否为空
			if (!StringUtils.isEmpty(queryStr)) {
				if (queryStr.indexOf("&") > -1) {
					String[] paramString = queryStr.split("&");
					for (String string : paramString) {
						if (string.indexOf(PROXY_PARAM)>-1 || string.indexOf(PROXY_PARAM_ENCODE)>-1) {
							continue;
						}
						reqQueryStr.append(string).append("&");// 参数
					}
					queryStr = reqQueryStr.toString();
					if (queryStr.lastIndexOf("&") > -1) {
						queryStr = queryStr.substring(0, queryStr.length() - 1);
					}
				} else if (queryStr.indexOf(PROXY_PARAM)==-1 || queryStr.indexOf(PROXY_PARAM_ENCODE)==-1) {
					queryStr = "";
				}
			}
			return queryStr;
		}

		@Override
        public long getDateHeader(String name) {
            return tryGetLong(headersNode, name);
        }

        @Override
        public String getContentType() {
            return tryGetString(headersNode, "Content-Type");
        }

        @Override
        public String getHeader(String name) {
            return tryGetString(headersNode, name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String value = getHeader(name);
            if (value != null)
                return Collections.enumeration(Collections.singletonList(value));
            return Collections.emptyEnumeration();
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return IteratorUtils.asEnumeration(headersNode.fieldNames());
        }

        @Override
        public String getAuthType() {
            return super.getAuthType();
        }

        @Override
        public String getCharacterEncoding() {
            return super.getCharacterEncoding();
        }

        @Override
        public int getContentLength() {
            return super.getContentLength();
        }

        @Override
        public int getIntHeader(String name) {
            return tryGetInt(headersNode, name);
        }

        @Override
        public String getMethod() {
            return this.method;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream byteArrayInputStream = body == null ? new ByteArrayInputStream(new byte[0]) : new ByteArrayInputStream(body.getBytes(defaultCharset));
            return new ServletInputStream() {
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(
                    this.getInputStream()));
        }
    }

    private static class HttpServletResponseWrapper extends
            javax.servlet.http.HttpServletResponseWrapper implements
            Serializable {

        private static final long serialVersionUID = -6823255025479924073L;

        public HttpServletResponseWrapper(HttpServletResponse response,
                                          OutputStream outputStream) {
            super(response);
            statusCode = 200;
            this.servletOutputStream = new ServletOutputStreamWrapper(outputStream);
        }

        public ServletOutputStream getOutputStream() {
            return servletOutputStream;
        }

        public void setStatus(int code) {
            statusCode = code;
            super.setStatus(200);
        }

        public void sendError(int i, String string) throws IOException {
            statusCode = i;
            super.sendError(i, string);
        }

        public void sendError(int i) throws IOException {
            statusCode = i;
            super.sendError(i);
        }

        public void sendRedirect(String string) throws IOException {
            statusCode = 302;
            super.sendRedirect(string);
        }

        public void setStatus(int code, String msg) {
            statusCode = code;
            super.setStatus(code);
        }

        public int getStatus() {
            return statusCode;
        }

        public void setContentLength(int length) {
            contentLength = length;
            super.setContentLength(length);
        }

        public int getContentLength() {
            return contentLength;
        }

        public void setContentType(String type) {
            contentType = type;
            super.setContentType(type);
        }

        public String getContentType() {
            return contentType;
        }

        public PrintWriter getWriter() throws IOException {
            if (writer == null)
                writer = new PrintWriter(new OutputStreamWriter(
                        servletOutputStream, getCharacterEncoding()), true);
            return writer;
        }

        public void addHeader(String name, String value) {
            String header[] = {name, value};
            headers.add(header);
            super.addHeader(name, value);
        }

        public void setHeader(String name, String value) {
            addHeader(name, value);
        }

        public Collection<String[]> getHeaders() {
            return headers;
        }

        public void addCookie(Cookie cookie) {
            cookies.add(cookie);
            super.addCookie(cookie);
        }

        public Collection<Cookie> getCookies() {
            return cookies;
        }

        public void flushBuffer() throws IOException {
            flush();
            super.flushBuffer();
        }

        public void reset() {
            super.reset();
            cookies.clear();
            headers.clear();
            statusCode = 200;
            contentType = null;
            contentLength = 0;
        }

        public void resetBuffer() {
            super.resetBuffer();
        }

        public void flush() throws IOException {
            if (writer != null)
                writer.flush();
            servletOutputStream.flush();
        }

        public String encodeRedirectUrl(String s) {
            return super.encodeRedirectURL(s);
        }

        public String encodeUrl(String s) {
            return super.encodeURL(s);
        }

        private int statusCode;
        private int contentLength;
        private String contentType;
        private final List<String[]> headers = new ArrayList<String[]>();
        private final List<Cookie> cookies = new ArrayList<Cookie>();
        private ServletOutputStream servletOutputStream;
        private PrintWriter writer;
    }

    private static class ServletOutputStreamWrapper extends ServletOutputStream {

        public ServletOutputStreamWrapper(OutputStream stream) {
            this.stream = stream;
        }

        public void write(int b) throws IOException {
            stream.write(b);
        }

        public void write(byte b[]) throws IOException {
            stream.write(b);
        }

        public void write(byte b[], int off, int len) throws IOException {
            stream.write(b, off, len);
        }

        private OutputStream stream;

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }
    }


}
