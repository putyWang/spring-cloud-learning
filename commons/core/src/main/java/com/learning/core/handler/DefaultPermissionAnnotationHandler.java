package com.learning.core.handler;

import com.learning.core.annotation.Permission;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;

import java.lang.annotation.Annotation;

/**
 * 自定义注解授权处理器
 * 授权逻辑已在拦截器，这里直接复制父类逻辑即可
 */
public class DefaultPermissionAnnotationHandler
        extends AuthorizingAnnotationHandler {
    public DefaultPermissionAnnotationHandler() {
        super(Permission.class);
    }

    public void assertAuthorized(String permission) throws AuthorizationException {
        if (!this.getSubject().isPermitted(permission))
            throw new AuthorizationException("该用户没有该权限");
        ;
    }

    @Override
    public void assertAuthorized(Annotation a)
            throws AuthorizationException {

    }
}
