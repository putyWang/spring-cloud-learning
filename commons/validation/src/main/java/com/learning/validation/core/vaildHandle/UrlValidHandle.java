package com.learning.validation.core.vaildHandle;

import com.learning.validation.core.annotation.UrlValid;
import com.learning.validation.core.utils.ValidUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class UrlValidHandle implements ConstraintValidator<UrlValid, String> {
    private String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+(\\\\?{0,1}(([A-Za-z0-9-~]+\\\\={0,1})([A-Za-z0-9-~]*)\\\\&{0,1})*)$";

    public UrlValidHandle() {
    }

    public void initialize(UrlValid constraintAnnotation) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ValidUtil.validUrl(value);
    }
}
