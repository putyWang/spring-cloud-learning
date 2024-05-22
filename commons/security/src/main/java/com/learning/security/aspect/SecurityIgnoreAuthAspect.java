package com.learning.security.aspect;

import cn.hutool.core.util.StrUtil;
import com.learning.security.annotation.IgnoreAuth;
import com.learning.core.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lengleng
 * @date 2022-06-04
 *
 * 跳过权限验证接口需要内部调用
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class SecurityIgnoreAuthAspect implements Ordered {

    private final HttpServletRequest request;

    @SneakyThrows
    @Before("@within(ignoreAuth) || @annotation(ignoreAuth)")
    public void around(JoinPoint point, IgnoreAuth ignoreAuth) {
        // 实际注入的IgnoreAuth实体由表达式后一个注解决定，即是方法上的@IgnoreAuth注解实体，若方法上无@IgnoreAuth注解，则获取类上的
        if (ignoreAuth == null) {
            Class<?> clazz = point.getTarget().getClass();
            ignoreAuth = AnnotationUtils.findAnnotation(clazz, IgnoreAuth.class);
        }
        String header = request.getHeader(SecurityConstants.FROM);
        if (ignoreAuth.ignoreAuth() && ! StrUtil.equals(SecurityConstants.FROM_IN, header)) {
            log.warn("访问接口 {} 没有权限", point.getSignature().getName());
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}