package com.learning.validation.annotation;

import java.lang.annotation.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description 需要验证的参数的注解（对方法形参或形参所在类使用）
 * @date 2024-06-21
 **/
@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Valid {
}