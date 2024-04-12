package com.learning.core.annotation;

import java.lang.annotation.*;

/**
 * 方法权限注解
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * 权限控制值
     *
     * @return
     */
    String value() default "";

    /**
     * 描述
     *
     * @return
     */
    String notes() default "";
}
