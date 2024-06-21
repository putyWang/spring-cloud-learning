package com.learning.validation.core.annotation;

import com.learning.validation.core.vaildHandle.YhNotNullValidator;
import org.hibernate.validator.constraints.Length;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.*;
import java.lang.annotation.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
@Documented
@Constraint(
        validatedBy = {YhNotNullValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YhNotNullValid {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Size[] size() default {};

    Max[] max() default {};

    Min[] min() default {};

    Email[] email() default {};

    Pattern[] pattern() default {};

    EnumValue[] enumValue() default {};

    IdCardValid[] idCardValid() default {};

    PhoneNumberValid[] phoneNumberValid() default {};

    UrlValid[] urlValid() default {};

    Length[] length() default {};
}

