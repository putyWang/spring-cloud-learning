package com.learning.orm.core.conditions.query;

import cn.hutool.core.collection.CollUtil;
import com.learning.orm.core.conditions.AbstractLambdaWrapper;
import com.learning.orm.core.conditions.SharedString;
import com.learning.orm.core.conditions.interfaces.SFunction;
import com.learning.orm.core.conditions.segments.MergeSegments;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lambda 语法使用 Wrapper
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public class LambdaQueryWrapper<T> extends AbstractLambdaWrapper<T, LambdaQueryWrapper<T>>
        implements Query<LambdaQueryWrapper<T>, T, SFunction<T, ?>> {

    /**
     * 查询字段
     */
    private SharedString sqlSelect = new SharedString();

    public LambdaQueryWrapper() {
        this((T) null);
    }

    public LambdaQueryWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
    }

    public LambdaQueryWrapper(Class<T> entityClass) {
        super.setEntityClass(entityClass);
        super.initNeed();
    }

    LambdaQueryWrapper(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
                       Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                       SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.paramAlias = paramAlias;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    @Override
    public LambdaQueryWrapper<T> select(boolean condition, List<SFunction<T, ?>> columns) {
        return doSelect(condition, columns);
    }

    @Override
    @SafeVarargs
    public final LambdaQueryWrapper<T> select(SFunction<T, ?>... columns) {
        return doSelect(true, CollUtil.toList(columns));
    }

    @Override
    @SafeVarargs
    public final LambdaQueryWrapper<T> select(boolean condition, SFunction<T, ?>... columns) {
        return doSelect(condition, CollUtil.toList(columns));
    }

    /**
     * @since 3.5.4
     */
    protected LambdaQueryWrapper<T> doSelect(boolean condition, List<SFunction<T, ?>> columns) {
        if (condition && CollUtil.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(columnsToString(false, columns));
        }
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect.getStringValue();
    }

    /**
     * 用于生成嵌套 sql
     * <p>故 sqlSelect 不向下传递</p>
     */
    @Override
    protected LambdaQueryWrapper<T> instance() {
        return new LambdaQueryWrapper<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
                new MergeSegments(), paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSelect.toNull();
    }
}
