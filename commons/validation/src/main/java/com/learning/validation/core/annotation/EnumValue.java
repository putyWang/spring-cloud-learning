package com.learning.validation.core.annotation;

import com.learning.validation.core.vaildHandle.EnumValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public @interface EnumValue {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Constraint(
            validatedBy = {EnumValueValidator.class}
    )
    @Repeatable(EnumValue.List.class)
    public @interface EnumValue {
        String message() default "必须为指定值";

        String[] strValues() default {};

        int[] intValues() default {};

        Class<?> enumValue() default Class.class;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        public @interface List {
            EnumValue[] value();
        }
    }
}
