package com.learning.validation.core.yhclasscheck;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public interface IClassCheck<T> {
    CheckResult check(T t);
}
