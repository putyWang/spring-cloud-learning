package com.learning.job.schedule.config;

import com.learning.job.schedule.interceptor.CookieInterceptor;
import com.learning.job.schedule.interceptor.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private PermissionInterceptor permissionInterceptor;
    @Resource
    private CookieInterceptor cookieInterceptor;

    public WebMvcConfig() {
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.permissionInterceptor).addPathPatterns(new String[]{"/**"});
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(this.cookieInterceptor).addPathPatterns(new String[]{"/**"});
        interceptorRegistration.excludePathPatterns(new String[]{"/static/**", "/**/*.svg"});
    }
}
