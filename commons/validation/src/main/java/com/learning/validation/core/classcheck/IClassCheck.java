package com.learning.validation.core.classcheck;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public interface IClassCheck {

    /**
     * 匿名类验证方法
     *
     * @param t 验证对象
     * @return 验证结果
     */
    CheckResult check(Object t);
}
