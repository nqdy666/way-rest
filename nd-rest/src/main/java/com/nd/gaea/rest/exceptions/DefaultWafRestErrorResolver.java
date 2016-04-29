package com.nd.gaea.rest.exceptions;

import com.nd.gaea.client.exception.ResponseErrorMessage;
import com.nd.gaea.rest.exceptions.rest.AbstractRestErrorHandler;
import com.nd.gaea.util.WafJsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author vime
 * @since 0.9.6
 */
public class DefaultWafRestErrorResolver extends AbstractRestErrorHandler implements WafErrorResolver, Ordered {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Override
    public boolean process(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
    	if(throwable!=null){
    		logger.error(throwable.getMessage(), throwable);
    	}
        ResponseEntity<ResponseErrorMessage> entity = process(throwable, request);

        try {
            response.setStatus(entity.getStatusCode().value());
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print(WafJsonMapper.toJson(entity.getBody()));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
