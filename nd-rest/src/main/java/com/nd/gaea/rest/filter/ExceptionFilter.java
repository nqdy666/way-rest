package com.nd.gaea.rest.filter;

import com.nd.gaea.rest.exceptions.WafErrorResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author vime
 * @since 0.9.5
 */
public class ExceptionFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<WafErrorResolver> wafErrorResolvers;

    @Autowired
    public void setWafErrorResolvers(List<WafErrorResolver> wafErrorResolvers) {
        this.wafErrorResolvers = wafErrorResolvers;
    }

    protected List<WafErrorResolver> getWafErrorResolvers(){
        return wafErrorResolvers;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		logger.debug("Exception filter start");
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            if (ex instanceof NestedServletException && ex.getCause() != null){
                ex = (Exception) ex.getCause();
            }
            for (WafErrorResolver wafErrorResolver : wafErrorResolvers) {
                try {
                    if (wafErrorResolver.process(ex, request, response))
                        break;
                } catch (Exception e) {
                    ex = e;
                }
            }
        }
		logger.debug("Exception filter end");
    }
}
