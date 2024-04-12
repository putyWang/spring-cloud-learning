package com.learning.web.annotation;


import com.learning.web.eums.LogTypeEnum;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @author felix
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    String value() default "";

    LogTypeEnum type() default LogTypeEnum.QUERY;
}
