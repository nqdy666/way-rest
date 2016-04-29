package com.nd.gaea.rest.exceptions.rest;

import com.nd.gaea.WafException;
import com.nd.gaea.client.WafResourceAccessException;
import com.nd.gaea.rest.exceptions.extendExceptions.WafUcAuthenticationException;
import com.nd.gaea.rest.security.authentication.WafAuthenticationException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * @author vime
 * @since 0.9.6
 */
public enum RestErrorMappings {
    // 默认错误
    DEFAULT_ERROR(Exception.class, new DefaultRestErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR)),
    // 404错误异常
    NO_SUCH_REQUEST_HANDLING_METHOD(NoSuchRequestHandlingMethodException.class, new DefaultRestErrorHandler(HttpStatus.NOT_FOUND, "WAF/URI_NOT_FOUND")),
    // 请求方法不被支持
    HTTP_METHOD_NOT_SUPPORTED(HttpRequestMethodNotSupportedException.class, new DefaultRestErrorHandler(HttpStatus.METHOD_NOT_ALLOWED, "WAF/METHOD_NOT_ALLOWED")),
    // 媒体类型不被支持
    HTTP_MEDIA_TYPE_NOT_SUPPORTED(HttpMediaTypeNotSupportedException.class, new DefaultRestErrorHandler(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "WAF/UNSUPPORTED_MEDIA_TYPE")),
    // 方法参数验证异常
    METHOD_ARGUMENT_NOT_VALID(MethodArgumentNotValidException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST, "WAF/INVALID_ARGUMENT")),
    // 绑定异常
    BIND_ERROR(BindException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST, "WAF/INVALID_ARGUMENT")),
    // 转换异常
    CONVERSION_NOT_SUPPORT(ConversionNotSupportedException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST, "WAF/BAD_REQUEST")),
    // 请求资源类型无法接受
    HTTP_MEDIA_TYPE_NOT_ACCEPTABLE(HttpMediaTypeNotAcceptableException.class, new DefaultRestErrorHandler(HttpStatus.NOT_ACCEPTABLE, "WAF/NOT_ACCEPTABLE")),
    // 读取异常
    HTTP_MESSAGE_NOT_READABLE(HttpMessageNotReadableException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST, "WAF/INVALID_ARGUMENT")),
    // 写入异常
    HTTP_MESSAGE_NOT_WRITABLE(HttpMessageNotWritableException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST)),
    // 请求参数缺失
    MISSING_REQUEST_PARAMETER(MissingServletRequestParameterException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST, "WAF/REQUIRE_ARGUMENT")),
    // 请求部分缺失
    MISSING_REQUEST_PART(MissingServletRequestPartException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST, "WAF/REQUIRE_ARGUMENT")),
    // 404错误
    NOT_FOUND(NoHandlerFoundException.class, new DefaultRestErrorHandler(HttpStatus.NOT_FOUND, "WAF/URI_NOT_FOUND")),
    // 请求绑定异常
    REQUEST_BIND_ERROR(ServletRequestBindingException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST, "WAF/BAD_REQUEST")),
    // 类型匹配异常
    TYPE_MISMATCH(TypeMismatchException.class, new DefaultRestErrorHandler(HttpStatus.BAD_REQUEST, "WAF/BAD_REQUEST")),
    // 访问拒绝
    ACCESS_DENIED(AccessDeniedException.class, new DefaultRestErrorHandler(HttpStatus.FORBIDDEN, "WAF/ACCESS_DENIED")),
    // 认证信息不全，或者头信息格式错误
    INSUFFICIENT_AUTHENTICATION(InsufficientAuthenticationException.class, new DefaultRestErrorHandler(HttpStatus.UNAUTHORIZED, "WAF/AUTH_INVALID_TOKEN")),
    // 认证凭据没找到
    AUTHENTICATION_CREDENTIAL_NOT_FOUND(AuthenticationCredentialsNotFoundException.class, new DefaultRestErrorHandler(HttpStatus.UNAUTHORIZED, "WAF/AUTH_ERROR")),
    // 认证错误
    WAF_AUTHENTICATION_ERROR(WafUcAuthenticationException.class, new DefaultRestErrorHandler(HttpStatus.UNAUTHORIZED)),
    WAF_AUTHENTICATION_EXCEPTION(WafAuthenticationException.class, new RemoteResponseSupportExceptionRestErrorHandler(HttpStatus.UNAUTHORIZED)),
    // 远程访问异常
    WAF_RESOURCE_ACCESS_ERROR(WafResourceAccessException.class, new RemoteResponseSupportExceptionRestErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR)),
    // WAF错误信息
    WAF_ERROR(WafException.class, new WafExceptionRestErrorHandler());

    private Class throwableClass;
    private AbstractRestErrorHandler handler;

    RestErrorMappings(final Class throwableClass, final AbstractRestErrorHandler handler) {
        this.throwableClass = throwableClass;
        this.handler = handler;
    }

    public Class getThrowableClass() {
        return throwableClass;
    }

    public AbstractRestErrorHandler getHandler() {
        return handler;
    }
}
