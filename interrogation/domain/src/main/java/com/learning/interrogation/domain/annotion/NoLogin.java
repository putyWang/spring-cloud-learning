package com.learning.interrogation.domain.annotion;

import java.lang.annotation.*;

/**
 * NoLogin
 * 不需要登录验证 在不需要登录就能调用的接口使用 一般用在POST请求接口上
 *
 * @author lihaoru
 * @date 2021-10-12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLogin {
}
