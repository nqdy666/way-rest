package com.nd.gaea.rest.config;

import com.nd.gaea.WafProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author vime
 * @since 0.9.6
 */
class WafPropertiesListener implements ApplicationListener<ContextRefreshedEvent> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static int count = 0;
    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
	
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        WafProperties.getProperties().list(printWriter);
        if (count==0) {
        	logger.info(result.toString());
        	count++;
		}
    }
}
