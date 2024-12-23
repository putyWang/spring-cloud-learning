package com.learning.core.domain.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UnionUniqueCodes.class)
@Documented
public @interface UnionUniqueCode {
    String group();

    String code();
}
