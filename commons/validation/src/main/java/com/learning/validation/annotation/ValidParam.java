package com.learning.validation.annotation;

import java.lang.annotation.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description 方法是否需要参数验证
 * @date 2024-06-21
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidParam {
}
