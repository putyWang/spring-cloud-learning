package com.learning.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.core.enums.ApiCode;
import com.learning.core.utils.ObjectUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义过滤器，处理shiro重定向问题
 *
 * @author sunqiyan
 */
@Log4j2
public class CustomAuthenticationFilter extends FormAuthenticationFilter {

    /**
     * 对跨域提供支持
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        log.info("request接口地址：{}", ((HttpServletRequest) request).getRequestURI());
        log.info("request接口请求方式：{}", ((HttpServletRequest) request).getMethod());
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();

        if (ObjectUtils.isEmpty(principal)) {
            log.error("当前用户未未登陆，响应 code 编码为{}", ApiCode.LOGIN_NOT.getCode());
            try {
                //设置前端相关属性
                ApiResult<Boolean> fail = ApiResult.fail(ApiCode.LOGIN_NOT);
                HttpServletResponse servletResponse = (HttpServletResponse) response;
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.setContentType("application/json;charset=UTF-8");
                servletResponse.setHeader("Access-Control-Allow-Origin", "*");
                //将失败结果返回前端
                response.getWriter().write(new ObjectMapper().writeValueAsString(fail));
            } catch (java.io.IOException IOException) {
                IOException.printStackTrace();
            }
        }

        return false;
    }
}
