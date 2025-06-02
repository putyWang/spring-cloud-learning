package com.learning.interrogation.domain.annotion;

import java.lang.annotation.*;

/**
 * Login
 * 需要登录验证 一般用在GET请求接口上
 *
 * @author lihaoru
 * @date 3/30/22
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Login {
}
