package com.nd.gaea.rest.exceptions.friendly;

import com.nd.gaea.WafException;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author vime
 * @since 0.9.6
 */
@Component
@Order(100)
public class WafExceptionFriendlyResolver extends FriendlyExceptionMessageConverter {
    @Override
    public boolean convert(ResponseEntity<ResponseErrorMessage> responseEntity) {
        Throwable throwable = responseEntity.getBody().getThrowable();
        if(throwable instanceof WafException)
        {
            //不做任何转换
            return true;
        }
        return false;
    }
}
