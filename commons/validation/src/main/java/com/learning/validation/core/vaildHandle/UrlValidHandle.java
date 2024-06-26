package com.learning.validation.core.vaildHandle;

import com.learning.validation.core.annotation.UrlValid;
import com.learning.validation.core.utils.ValidUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description Url 验证（UrlValid）处理器
 * @date 2024-06-21
 **/
public class UrlValidHandle implements ConstraintValidator<UrlValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ValidUtil.validUrl(value);
    }
}
