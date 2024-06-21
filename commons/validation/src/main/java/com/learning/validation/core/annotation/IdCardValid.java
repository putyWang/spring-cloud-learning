package com.learning.validation.core.annotation;

import com.learning.validation.core.vaildHandle.IdCardValidHandle;

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
        validatedBy = {IdCardValidHandle.class}
)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdCardValid {
    String message() default "身份证号码错误,请核实!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}