package com.nd.gaea.rest.exceptions;

import com.nd.gaea.client.exception.ResponseErrorMessage;
import com.nd.gaea.rest.exceptions.friendly.FriendlyExceptionMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author vime
 * @since 0.9.6
 */
public class FriendlyWafRestErrorResolver extends WafRestErrorResolver {
    private List<FriendlyExceptionMessageConverter> converters;

    @Autowired
    public void setConverters(List<FriendlyExceptionMessageConverter> converters) {
        this.converters = converters;
    }

    @Override
    protected ResponseEntity<ResponseErrorMessage> process(Throwable throwable, HttpServletRequest request) {
        ResponseEntity<ResponseErrorMessage> responseEntity = super.process(throwable, request);
        for (FriendlyExceptionMessageConverter converter : converters) {
            if (converter.convert(responseEntity))
                break;
        }
        return responseEntity;
    }
}
