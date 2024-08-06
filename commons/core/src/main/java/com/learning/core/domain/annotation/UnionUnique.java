package com.learning.core.domain.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UnionUnique {

    /**
     * 组名称
     *
     * @return
     */
    String group() default "";

    /**
     * 表名
     *
     * @return
     */
    String column() default "";
}
