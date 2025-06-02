package com.learning.core.domain.annotation;

import java.lang.annotation.*;

/**
 * @author root
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnionUniqueCodes {
    UnionUniqueCode[] value();
}

