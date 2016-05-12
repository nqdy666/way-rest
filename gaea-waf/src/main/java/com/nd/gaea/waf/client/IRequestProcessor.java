package com.nd.gaea.waf.client;

/**
 * 用于对请求进行统一的处理，如 附加安全的 GaeaHttpClient\Zuul proxy\Feign client
 * Created by vime on 2016/2/19.
 */
public interface IRequestProcessor {
    void process(IRequest request);
}
