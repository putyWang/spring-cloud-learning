package com.learning.core.handler;

import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;

/**
 * 自定义shiro的AOP拦截器
 * 用以注入自定义的授权拦截器
 */
public class DefaultPermissionAnnotationAopInterceptor
        extends AopAllianceAnnotationsAuthorizingMethodInterceptor {

    public DefaultPermissionAnnotationAopInterceptor() {
        //注入原注解授权拦截器
        super();
        //注入自定义注解授权拦截器
        this.methodInterceptors.add(new DefaultPermissionAnnotationMethodInterceptor());
    }
}
