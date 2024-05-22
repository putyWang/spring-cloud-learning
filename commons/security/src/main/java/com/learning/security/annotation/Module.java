package com.learning.security.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 权限控制类注解
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    String name() default "";

    String code() default "";

    /**
     * 权限控制值
     *
     * @return
     */
    @AliasFor("name")
    String value() default "";
}
