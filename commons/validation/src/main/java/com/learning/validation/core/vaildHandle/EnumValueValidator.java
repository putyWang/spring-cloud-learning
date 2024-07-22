package com.learning.validation.core.vaildHandle;

import cn.hutool.core.util.ArrayUtil;
import com.learning.validation.core.annotation.EnumValue;
import com.learning.validation.core.function.EnumValueValidFunction;
import lombok.SneakyThrows;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    /**
     * 匹配的 String 类型数组
     */
    private String[] strValues;

    /**
     * 匹配的 int 类型数组
     */
    private int[] intValues;

    /**
     * 匹配的枚举类型
     */
    private Class<? extends EnumValueValidFunction> cls;

    @Override
    public void initialize(EnumValue enumValue) {
        this.strValues = enumValue.strValues();
        this.intValues = enumValue.intValues();
        this.cls = enumValue.enumValue();
    }

    @SneakyThrows
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }
        // 1 判断是否为指定枚举值
        if (this.cls != null && this.cls.isEnum()) {
            for(EnumValueValidFunction obj : this.cls.getEnumConstants()) {
                if (obj.containsCode(String.valueOf(value))) {
                    return true;
                }
            }
        } else {
            // 2 判断是否包含指定字符串
            return (value instanceof String && ArrayUtil.contains(strValues, (String)value)) ||
            // 3 判断是否包含指定数字
                    (value instanceof Integer && ArrayUtil.contains(intValues, (Integer)value));
        }

        return false;
    }
}

