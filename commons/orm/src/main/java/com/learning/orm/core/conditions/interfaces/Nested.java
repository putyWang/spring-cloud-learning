package com.learning.orm.core.conditions.interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午10:33
 */
public interface Nested<Param, Children> extends Serializable {
    default Children and(Consumer<Param> consumer) {
        return this.and(true, consumer);
    }

    Children and(boolean condition, Consumer<Param> consumer);

    default Children or(Consumer<Param> consumer) {
        return this.or(true, consumer);
    }

    Children or(boolean condition, Consumer<Param> consumer);

    default Children nested(Consumer<Param> consumer) {
        return this.nested(true, consumer);
    }

    Children nested(boolean condition, Consumer<Param> consumer);

    default Children not(Consumer<Param> consumer) {
        return this.not(true, consumer);
    }

    Children not(boolean condition, Consumer<Param> consumer);
}
