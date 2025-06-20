package com.learning.orm.core.annotation;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.GenericConverter;

import java.lang.annotation.*;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/8 下午6:05
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface R2dbcField {

    String name() default "";

    GenericConverter convert();
}
