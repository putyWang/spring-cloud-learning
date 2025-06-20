package com.learning.orm.core.conditions.interfaces;

import java.io.Serializable;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午10:33
 */
public interface Join<Children> extends Serializable {
    default Children or() {
        return this.or(true);
    }

    Children or(boolean condition);

    default Children apply(String applySql, Object... values) {
        return this.apply(true, applySql, values);
    }

    Children apply(boolean condition, String applySql, Object... values);

    default Children last(String lastSql) {
        return this.last(true, lastSql);
    }

    Children last(boolean condition, String lastSql);

    default Children comment(String comment) {
        return this.comment(true, comment);
    }

    Children comment(boolean condition, String comment);

    default Children first(String firstSql) {
        return this.first(true, firstSql);
    }

    Children first(boolean condition, String firstSql);

    default Children exists(String existsSql, Object... values) {
        return this.exists(true, existsSql, values);
    }

    Children exists(boolean condition, String existsSql, Object... values);

    default Children notExists(String existsSql, Object... values) {
        return this.notExists(true, existsSql, values);
    }

    Children notExists(boolean condition, String existsSql, Object... values);
}

