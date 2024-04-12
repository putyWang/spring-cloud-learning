package com.learning.core.handler;

import com.learning.core.annotation.IgnoreAuth;
import com.learning.core.annotation.Module;
import com.learning.core.annotation.Permission;
import com.learning.core.utils.ObjectUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 自定义shiro注解授权处理类
 */
public class DefaultAuthorizationAttributeSourceAdvisor
        extends AuthorizationAttributeSourceAdvisor {

    /**
     * 注入自定义权限注解
     */
    private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES = new Class[]{
            IgnoreAuth.class, Permission.class, Module.class,
            RequiresPermissions.class, RequiresRoles.class,
            RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class
    };

    public DefaultAuthorizationAttributeSourceAdvisor() {
        setAdvice(new DefaultPermissionAnnotationAopInterceptor());
    }

    /**
     * 判断是否存在相关注解
     *
     * @param method
     * @param targetClass
     * @return
     */
    @Override
    public boolean matches(Method method, Class targetClass) {
        Method m = method;

        if (isAuthzAnnotationPresent(m)) {
            return true;
        }

        if (!ObjectUtils.isEmpty(targetClass)) {
            try {
                m = targetClass.getMethod(m.getName(), m.getParameterTypes());
                return isAuthzAnnotationPresent(m) || isAuthzAnnotationPresent(targetClass);
            } catch (NoSuchMethodException ignored) {

            }
        }

        return false;
    }

    /**
     * 判断是否存在类注解Model
     *
     * @param targetClazz
     * @return
     */
    private boolean isAuthzAnnotationPresent(Class<?> targetClazz) {
        for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
            Annotation a = AnnotationUtils.findAnnotation(targetClazz, annClass);
            if (a != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在方法注解Permission
     *
     * @param method
     * @return
     */
    private boolean isAuthzAnnotationPresent(Method method) {
        for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
            Annotation a = AnnotationUtils.findAnnotation(method, annClass);
            if (a != null) {
                return true;
            }
        }
        return false;
    }
}
