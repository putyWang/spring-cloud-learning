package com.learning.transaction.annotation;

import java.lang.annotation.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description 用于外部接口访问回调
 * @date 2024-06-21
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallBack {

    String name();

    String successMethod() default "success";

    String failMethod() default "fail";
}