/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */

package com.nd.gaea.rest.config;


import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;


/**
 * 提供默认的 security config
 */
public class WafWebSecurityConfigurerAdapter extends AbstractWebSecurityConfigurerAdapter {


    @Override
    protected void onConfigure(HttpSecurity http) throws Exception {
    }

	@Override
	public void init(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/$waf/**").antMatchers(HttpMethod.OPTIONS, "/**");
		super.init(web);
	}
    
    
}
