package com.nd.gaea.rest.security;

import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Order(50)
public class WafSecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
}
