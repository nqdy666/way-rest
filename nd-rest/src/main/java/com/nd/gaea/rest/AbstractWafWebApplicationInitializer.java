/*
 * Copyright 2014 NetDragon  <leizhang1223@gmail.com>.
 */
package com.nd.gaea.rest;

import com.nd.gaea.WafProperties;
import com.nd.gaea.rest.config.WafWebSecurityConfigurerAdapter;
import com.nd.gaea.rest.filter.WafCorsFilter;
import com.nd.gaea.rest.filter.WafHttpMethodOverrideFilter;
import com.nd.gaea.rest.security.services.impl.RealmServiceImpl;
import com.nd.gaea.rest.support.WafContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import javax.servlet.ServletRegistration.Dynamic;
import java.util.EnumSet;

/**
 * web项目的启动配置 1、设置系统realm名称
 *
 * @author johnny
 */
@Order(1)
public abstract class AbstractWafWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {

        return new String[]{"/"};
    }

    /**
     * 获取当前安全模块的鉴权行为是否被禁用
     *
     * @return
     * @deprecated 请使用 {@link WafContext#isSecurityDisabled }
     */
    @Deprecated
    public boolean isDisableSecurity() {
        return WafContext.isSecurityDisabled();
    }

    /**
     * 设置当前安全模块的鉴权行为禁用状态。
     *
     * @param disableSecurity
     * @deprecated 请参考 {@link WafContext#isSecurityDisabled}
     */
    @Deprecated
    public void setDisableSecurity(boolean disableSecurity) {
        WafProperties.setProperty(WafContext.WAF_SECURITY_DISABLED, String.valueOf(disableSecurity));
    }


    /**
     * 获得应用当前环境的信息
     *
     * @return
     * @deprecated 请使用 {@link WafContext#getEnvironment}
     */
    @Deprecated
    public String getProductStage() {
        return WafContext.getEnvironment();
    }


    /**
     * 设置应用当前环境的信息
     *
     * @param productStage
     * @deprecated 请使用 {@link WafContext#setEnvironment}
     */
    @Deprecated
    public void setProductStage(String productStage) {
        WafContext.setEnvironment(productStage);
    }

    /**
     * 設置当spring没有找到Handler的时候，抛出NoHandlerFountException异常。并且被异常捕获到。统一进行处理
     */
    @Override
    protected void customizeRegistration(Dynamic registration) {
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
    }

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {

        //保持兼容性
        WafProperties.getDefaultProperties().setProperty(RealmServiceImpl.WAF_UC_REALM, getRealm());
        super.onStartup(servletContext);
        initFilters(servletContext);
        registerFilters(servletContext);
    }
    
    private void initFilters(ServletContext servletContext) {
    	addFilter(servletContext, "exceptionFilter", new DelegatingFilterProxy("exceptionFilter"));

        initCharacterEncodingFilter(servletContext);
        addFilter(servletContext, "wafCorsFilter", new WafCorsFilter());
        addFilter(servletContext, "wafHttpMethodOverrideFilter", new WafHttpMethodOverrideFilter());
    }
    
    protected void registerFilters(ServletContext servletContext) {
    }
    
    /**
     * 
    * @Title: initCharacterEncodingFilter 
    * @Description:  字符编码过滤器
    * @param @param servletContext    设定文件 
    * @return void    返回类型 
    * @throws
     */
    protected void initCharacterEncodingFilter(ServletContext servletContext) {
	    CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
	    //characterEncodingFilter.setForceEncoding(true);
	    characterEncodingFilter.setEncoding("UTF-8");
	    addFilter(servletContext, "characterEncodingFilter", characterEncodingFilter);
    }

    protected void addFilter(ServletContext servletContext, String filterName, Filter filter) {
        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(filterName, filter);
        filterRegistration.setAsyncSupported(isAsyncSupported());
        filterRegistration.addMappingForUrlPatterns(getDispatcherTypes(), false, "/*");
    }

    protected EnumSet<DispatcherType> getDispatcherTypes() {
        return isAsyncSupported() ?
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) :
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    /**
     * @deprecated 请使用新的配置方案，参阅 http://doc.sdp.nd/index.php?title=waf.properties
     */
    @Deprecated
    protected void initUcConfig() {
    }

    /**
     * 默认服务启动，加载的权限验证模块实现类
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{WafWebSecurityConfigurerAdapter.class};
    }

    /**
     * 扩展实现，进行设置realm参数
     *
     * @return
     * @deprecated 请使用新的配置方案，参阅 http://doc.sdp.nd/index.php?title=waf.properties
     */
    @Deprecated
    public String getRealm() {
        return "";
    }

}
