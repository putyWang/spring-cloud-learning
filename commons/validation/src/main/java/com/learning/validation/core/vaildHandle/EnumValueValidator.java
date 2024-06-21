package com.learning.validation.core.vaildHandle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

import static  com.learning.validation.core.annotation.EnumValue.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    private String[] strValues;
    private int[] intValues;
    private Class<?> cls;

    @Override
    public void initialize(EnumValue enumValue) {
        this.strValues = enumValue.strValues();
        this.intValues = enumValue.intValues();
        this.cls = enumValue.enumValue();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            if (null == value) {
                return true;
            } else {
                if (this.cls != null && this.cls.isEnum()) {
                    for(Object obj : this.cls.getEnumConstants()) {
                        Method method = this.cls.getDeclaredMethod("getCode");
                        String expectValue = String.valueOf(method.invoke(obj));

                        if (expectValue.equals(String.valueOf(value))) {
                            return true;
                        }
                    }
                } else {
                    if (value instanceof String) {
                        for(String s : strValues) {
                            if (s.equals(value)) {
                                return true;
                            }
                        }
                    } else if (value instanceof Integer) {
                        for(Integer s : intValues) {
                            if (s == value) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

