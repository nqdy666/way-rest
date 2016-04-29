import com.nd.gaea.rest.exceptions.WafRestErrorResolver;
import feign.FeignException;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by way on 2016/04/28.
 * <p>
 * 拆解掉FeignException中包装过的异常
 */
public class WafFeignErrorResolver extends WafRestErrorResolver {


    @Override
    public boolean process(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(throwable);
        Assert.notNull(request);
        Assert.notNull(response);
        if (!(throwable instanceof FeignException)) {
            return false;
        }
        try {
            String message = throwable.getMessage();
            response.setContentType("application/json;charset=UTF-8");
            String statusPrefix = "status ";
            String content = "; content:\n{";
            if (!message.startsWith(statusPrefix)) {
                return false;
            }
            if (message.indexOf(content) == -1) {
                return false;
            }
            String statusSuffix = " reading";
            String status = message.substring(message.indexOf(statusPrefix) + statusPrefix.length(), message.indexOf(statusSuffix));
            response.setStatus(Integer.parseInt(status));
            PrintWriter writer = response.getWriter();
            writer.print(message.substring(message.indexOf(content) + content.length() - 1));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
