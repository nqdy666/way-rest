import com.nd.gaea.WafException;
import com.nd.gaea.client.exception.ResponseErrorMessage;
import com.nd.gaea.util.WafJsonMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author way
 *         Created on 2016/5/13.
 */
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            ResponseErrorMessage errorMessage = WafJsonMapper.parse(response.body().asInputStream(),ResponseErrorMessage.class);
            return new WafException(errorMessage, HttpStatus.valueOf(response.status()));
        } catch (IOException e) {
            return new WafException("","",e);
        }
    }
}
