package com.nd.gaea.rest.exceptions.friendly;

import com.nd.gaea.I18NProvider;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author vime
 * @since 0.9.6
 */
@Component
public class DefaultFriendlyResolver extends FriendlyExceptionMessageConverter {
    @Override
    public boolean convert(ResponseEntity<ResponseErrorMessage> responseEntity) {
        int statusCode = responseEntity.getStatusCode().value();
        String name = "waf.er.status.code." + statusCode;
        if (I18NProvider.containsKey(name)) {
            updateErrorMessage(responseEntity, I18NProvider.getString(name));
            return true;
        }
        return false;
    }
}
