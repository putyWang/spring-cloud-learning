package com.learning.validation.core.vaildHandle;

import com.learning.validation.core.annotation.PhoneNumberValid;
import com.learning.validation.core.utils.ValidUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class PhoneNumberValidHandle implements ConstraintValidator<PhoneNumberValid, String> {
    public PhoneNumberValidHandle() {
    }

    public void initialize(PhoneNumberValid constraintAnnotation) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ValidUtil.validPhone(value);
    }
}
