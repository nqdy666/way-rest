package com.nd.gaea.waf.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by vime on 2016/2/25.
 */
@SpringBootApplication
public abstract class GaeaApplication extends SpringBootServletInitializer {
    public GaeaApplication() {
        setRegisterErrorPageFilter(false);
    }
}
