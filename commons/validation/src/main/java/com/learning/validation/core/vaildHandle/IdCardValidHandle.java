package com.learning.validation.core.vaildHandle;

import com.learning.validation.core.annotation.IdCardValid;
import com.learning.validation.core.utils.ValidUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class IdCardValidHandle implements ConstraintValidator<IdCardValid, Object> {
    public IdCardValidHandle() {
    }

    public void initialize(IdCardValid constraintAnnotation) {
    }

    public boolean isValid(Object objval, ConstraintValidatorContext context) {
        String value = (String)objval;
        return ValidUtil.validIdCard(value);
    }
}
