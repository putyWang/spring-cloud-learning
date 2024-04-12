package com.learning.core.handler;

import com.learning.core.annotation.IgnoreAuth;
import com.learning.core.annotation.Module;
import com.learning.core.annotation.Permission;
import com.learning.core.utils.ObjectUtils;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 自定义注解授权拦截器
 */
public class DefaultPermissionAnnotationMethodInterceptor
        extends AuthorizingAnnotationMethodInterceptor {

    public DefaultPermissionAnnotationMethodInterceptor() {
        //注入自定义注解授权处理器
        super(new DefaultPermissionAnnotationHandler());
    }

    /**
     * 权限验证方法
     *
     * @param mi
     * @throws AuthorizationException
     */
    public void assertAuthorized(MethodInvocation mi)
            throws AuthorizationException {

        //获取请求方法
        Method requestMethod = mi.getMethod();

        //获取当前访问类
        Class<?> targetClass = mi.getThis().getClass();

        try {
            IgnoreAuth ignoreAuth = targetClass.getAnnotation(IgnoreAuth.class);

            if (!ObjectUtils.isEmpty(ignoreAuth) && ignoreAuth.ignoreAuth()) {
                return;
            }

            ignoreAuth = requestMethod.getAnnotation(IgnoreAuth.class);

            //ignoreAuth为真时 该方法不需要权限验证
            if (!ObjectUtils.isEmpty(ignoreAuth) && ignoreAuth.ignoreAuth()) {
                return;
            }

            //因为需要方法所在的类，就直接在拦截器处理了授权认证了
            //自定义注解授权处理逻辑
            Annotation typeAnnotation = getAnnotation(mi);

            if (!(typeAnnotation instanceof Permission)) return;

            Permission permission = (Permission) typeAnnotation;
            //获取Controller注解model
            Module annotation = targetClass.getAnnotation(Module.class);

            //若类上没有Model注解，则表明无法访问
            if (annotation != null) {
                String method = requestMethod.getName();
                String base = annotation.value();
                ((DefaultPermissionAnnotationHandler) getHandler()).assertAuthorized(base + "_" + permission.value());
            } else
                throw new AuthorizationException("该用户没有该权限： " + requestMethod);
        } catch (AuthorizationException ae) {
            if (ae.getCause() == null) ae.initCause(new AuthorizationException("该用户没有该权限： " + requestMethod));
            throw ae;
        }
    }
}
