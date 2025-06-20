package com.learning.orm.core.conditions.interfaces;

import cn.hutool.core.collection.CollUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午10:34
 */
public interface Func<Children, R> extends Serializable {
    default Children isNull(R column) {
        return this.isNull(true, column);
    }

    Children isNull(boolean condition, R column);

    default Children isNotNull(R column) {
        return this.isNotNull(true, column);
    }

    Children isNotNull(boolean condition, R column);

    default Children in(R column, Collection<?> coll) {
        return this.in(true, column, coll);
    }

    Children in(boolean condition, R column, Collection<?> coll);

    default Children in(R column, Object... values) {
        return this.in(true, column, values);
    }

    Children in(boolean condition, R column, Object... values);

    default Children notIn(R column, Collection<?> coll) {
        return this.notIn(true, column, coll);
    }

    Children notIn(boolean condition, R column, Collection<?> coll);

    default Children notIn(R column, Object... values) {
        return this.notIn(true, column, values);
    }

    Children notIn(boolean condition, R column, Object... values);

    default Children inSql(R column, String sql) {
        return this.inSql(true, column, sql);
    }

    Children inSql(boolean condition, R column, String sql);

    Children gtSql(boolean condition, R column, String sql);

    default Children gtSql(R column, String sql) {
        return this.gtSql(true, column, sql);
    }

    Children geSql(boolean condition, R column, String sql);

    default Children geSql(R column, String sql) {
        return this.geSql(true, column, sql);
    }

    Children ltSql(boolean condition, R column, String sql);

    default Children ltSql(R column, String sql) {
        return this.ltSql(true, column, sql);
    }

    Children leSql(boolean condition, R column, String sql);

    default Children leSql(R column, String inValue) {
        return this.leSql(true, column, inValue);
    }

    default Children notInSql(R column, String inValue) {
        return this.notInSql(true, column, inValue);
    }

    Children notInSql(boolean condition, R column, String inValue);

    Children groupBy(boolean condition, R column);

    default Children groupBy(R column) {
        return this.groupBy(true, column);
    }

    Children groupBy(boolean condition, List<R> columns);

    default Children groupBy(List<R> columns) {
        return this.groupBy(true, columns);
    }

    default Children groupBy(R column, R... columns) {
        return this.groupBy(true, column, columns);
    }

    Children groupBy(boolean condition, R column, R... columns);

    Children groupBy(boolean condition, R column, List<R> columns);

    default Children orderByAsc(boolean condition, R column) {
        return this.orderBy(condition, true, column);
    }

    default Children orderByAsc(R column) {
        return this.orderByAsc(true, column);
    }

    default Children orderByAsc(boolean condition, List<R> columns) {
        return this.orderBy(condition, true, columns);
    }

    default Children orderByAsc(List<R> columns) {
        return this.orderByAsc(true, columns);
    }

    default Children orderByAsc(R column, R... columns) {
        return this.orderByAsc(true, column, columns);
    }

    default Children orderByAsc(boolean condition, R column, R... columns) {
        return this.orderBy(condition, true, column, columns);
    }

    default Children orderByAsc(boolean condition, R column, List<R> columns) {
        return this.orderBy(condition, true, column, columns);
    }

    default Children orderByDesc(boolean condition, R column) {
        return this.orderBy(condition, false, column);
    }

    default Children orderByDesc(R column) {
        return this.orderByDesc(true, column);
    }

    default Children orderByDesc(boolean condition, List<R> columns) {
        return this.orderBy(condition, false, columns);
    }

    default Children orderByDesc(List<R> columns) {
        return this.orderByDesc(true, columns);
    }

    default Children orderByDesc(R column, R... columns) {
        return this.orderByDesc(true, column, columns);
    }

    default Children orderByDesc(boolean condition, R column, R... columns) {
        return this.orderBy(condition, false, column, CollUtil.toList(columns));
    }

    default Children orderByDesc(boolean condition, R column, List<R> columns) {
        return this.orderBy(condition, false, column, columns);
    }

    Children orderBy(boolean condition, boolean isAsc, R column);

    Children orderBy(boolean condition, boolean isAsc, List<R> columns);

    Children orderBy(boolean condition, boolean isAsc, R column, R... columns);

    Children orderBy(boolean condition, boolean isAsc, R column, List<R> columns);

    default Children having(String sqlHaving, Object... params) {
        return this.having(true, sqlHaving, params);
    }

    Children having(boolean condition, String sqlHaving, Object... params);

    default Children func(Consumer<Children> consumer) {
        return this.func(true, consumer);
    }

    Children func(boolean condition, Consumer<Children> consumer);
}

