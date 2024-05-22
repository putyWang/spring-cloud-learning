package com.learning.security.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {
    /**
     * 不需要鉴权
     *
     * @return
     */
    boolean ignoreAuth() default true;
}
