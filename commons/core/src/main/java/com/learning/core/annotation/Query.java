package com.learning.core.annotation;

import com.learning.core.enums.QueryEnum;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    /**
     * 字段匹配方式，默认为eq
     *
     * @return
     */
    QueryEnum value() default QueryEnum.EQ;

    /**
     * 注解的位置
     *
     * @return
     */
    boolean where() default true;

    /**
     * 表字段名
     *
     * @return
     */
    String column() default "";

    ;
}
