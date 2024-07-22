package com.learning.validation.core.annotation;

import com.learning.validation.core.vaildHandle.ClassCheckHandle;
import com.learning.validation.core.classcheck.IClassCheck;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {ClassCheckHandle.class}
)
@Documented
@Repeatable(YHClassCheckList.class)
public @interface ClassCheck {
    String message() default "未指定验证类！";

    Class<? extends IClassCheck> checkClass();

    String checkPty() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
