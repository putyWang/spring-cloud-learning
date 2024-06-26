package com.learning.validation.core.vaildHandle;

import com.learning.validation.core.annotation.IdCardValid;
import com.learning.validation.core.utils.ValidUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description 身份证验证（IdCardValid）处理器
 * @date 2024-06-21
 **/
public class IdCardValidHandle implements ConstraintValidator<IdCardValid, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return ValidUtil.validIdCard((String)value);
    }
}
