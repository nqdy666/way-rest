package com.nd.gaea.rest.controller;

import com.nd.gaea.WafProperties;
import com.nd.gaea.client.support.WafContext;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author vime
 * @since 0.9.6
 */
@RestController
@RequestMapping("/$waf")
public class WafController {
    public static final String WAF_ACCESS_KEY = "waf.accessKey";

    static {
        WafProperties.getDefaultProperties().setProperty(WAF_ACCESS_KEY, "sdp.nd");
    }

    @RequestMapping(value = "{accessKey}/info", method = RequestMethod.GET)
    public void info(@PathVariable String accessKey, HttpServletResponse response) throws IOException {
        validAccessKey(accessKey, response);
        PrintWriter printWriter = response.getWriter();
        WafProperties.getProperties().list(printWriter);

        printWriter.println("-- listing waf environment --");
        printWriter.println("version: " + getClass().getPackage().getImplementationVersion());
        printWriter.println("isDebugMode: " + WafContext.isDebugMode());
        printWriter.println("isTraceEnabled: " + WafContext.isTraceEnabled());

        response.flushBuffer();
    }

//    @RequestMapping(value = "{accessKey}/set", method = RequestMethod.GET)
//    public void set(@RequestParam String key, @RequestParam String value, @PathVariable String accessKey, HttpServletResponse response) throws IOException {
//        validAccessKey(accessKey, response);
//        WafProperties.setProperty(key, value);
//        info(accessKey, response);
//    }


    @RequestMapping(value = "{accessKey}/trace/{value}", method = RequestMethod.GET)
    public void trace(@PathVariable String accessKey, @PathVariable String value, HttpServletResponse response) throws IOException {
        validAccessKey(accessKey, response);
        WafProperties.setProperty(com.nd.gaea.rest.support.WafContext.WAF_TRACE_ENABLED, value);
        info(accessKey, response);
    }

    void validAccessKey(String accessKey, HttpServletResponse response) throws IOException {
        if (!WafProperties.getProperty(WAF_ACCESS_KEY).equals(accessKey)) {
            response.sendError(401);
        }
    }
}
