package com.learning.orm.core.conditions.interfaces;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午11:00
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
