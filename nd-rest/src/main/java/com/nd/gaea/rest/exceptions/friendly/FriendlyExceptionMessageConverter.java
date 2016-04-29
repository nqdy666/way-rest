package com.nd.gaea.rest.exceptions.friendly;

import com.nd.gaea.client.exception.ResponseErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

/**
 * @author vime
 * @since 0.9.6
 */
public abstract class FriendlyExceptionMessageConverter {
    public abstract boolean convert(ResponseEntity<ResponseErrorMessage> responseEntity);

    protected void updateErrorMessage(ResponseEntity<ResponseErrorMessage> responseEntity, String message) {
        ResponseErrorMessage errorMessage = responseEntity.getBody();

        String detail = "Message:" + errorMessage.getMessage();
        String srcDetail = errorMessage.getDetail();
        if (StringUtils.hasText(srcDetail)) {
            detail += "\r\n" + srcDetail;
        }
        errorMessage.setDetail(detail);
        errorMessage.setMessage(message);
    }
}
