package com.nd.gaea.client.http;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.nd.gaea.WafException;
import com.nd.gaea.client.WafResourceAccessException;
import com.nd.gaea.client.exception.ErrorMessage;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import com.nd.gaea.util.WafJsonMapper;

/**
 * 异常处理类
 *
 * @author vime
 * @since 0.9.5
 */
class RestApiErrorHandler extends DefaultResponseErrorHandler {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        WafClientHttpResponse wafResponse = (WafClientHttpResponse) response;
        ResponseErrorMessage errorMessage = null;
        HttpHeaders headers = null;
        HttpStatus statusCode;

        headers = wafResponse.getHeaders();
        statusCode = wafResponse.getStatusCode();
        if (statusCode.value()==404) {
        	String detail = "REST api \"" + wafResponse.getHttpMethod() + " " + wafResponse.getUri() + " 无法访问。";
			throw new WafException("WAF/API_NOT_FOUND", "waf.er.resourceAccess.apiNotFound.exception", detail, HttpStatus.INTERNAL_SERVER_ERROR);
		}
        
        String responseText = IOUtils.toString(wafResponse.getBody(), "UTF-8");
        try {
            errorMessage = WafJsonMapper.parse(responseText, ResponseErrorMessage.class);
        }catch(JsonParseException ex){
        	String detail = "exception:"+ex.getClass()+",将 REST api \"" + wafResponse.getHttpMethod() + " " + wafResponse.getUri() + "\" 的响应内容 \"" + responseText + "\" 解析为Json发生异常。";
            log.error(detail, ex);
            throw new WafException("WAF/INTERNAL_SERVER_ERROR", "waf.er.resourceAccess.parse.exception", detail, HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }catch (IOException ex) {
            String detail = "exception:"+ex.getClass()+",将 REST api \"" + wafResponse.getHttpMethod() + " " + wafResponse.getUri() + "\" 的响应内容 \"" + responseText + "\" 转换为 " + ErrorMessage.class.toString() + " 发生异常。";
            log.error(detail, ex);
            throw new WafException("WAF/INTERNAL_SERVER_ERROR", "waf.er.resourceAccess.parse.exception", detail, HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
        log.warn("Remote rest api response error. Response content: ", responseText);
        throw new WafResourceAccessException(wafResponse.getHttpMethod(), wafResponse.getUri(), new ResponseEntity<>(errorMessage, headers, statusCode));
    }
}
