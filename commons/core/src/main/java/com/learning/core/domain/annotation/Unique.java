package com.learning.core.domain.annotation;

import com.learning.core.domain.enums.ApiCode;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {

    /**
     * 表字段
     *
     * @return
     */
    String column() default "";

    /**
     * 问题message
     *
     * @return
     */
    String code() default "";

    /**
     * 问题代码
     *
     * @return
     */
    ApiCode apiCode() default ApiCode.NULL;
}
