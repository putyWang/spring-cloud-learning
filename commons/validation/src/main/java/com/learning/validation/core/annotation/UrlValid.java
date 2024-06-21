package com.learning.validation.core.annotation;

import com.learning.validation.core.vaildHandle.UrlValidHandle;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
@Documented
@Constraint(
        validatedBy = {UrlValidHandle.class}
)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlValid {
    String message() default "传入地址有误,请核实!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
