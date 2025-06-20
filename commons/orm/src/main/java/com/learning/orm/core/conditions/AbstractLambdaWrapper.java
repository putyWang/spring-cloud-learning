package com.learning.orm.core.conditions;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.learning.orm.core.conditions.interfaces.SFunction;
import com.learning.orm.core.constant.StringPool;
import com.learning.orm.core.metadata.ColumnCache;
import com.learning.orm.core.metadata.LambdaMeta;
import com.learning.orm.util.LambdaUtils;
import com.learning.orm.util.sql.PropertyNamer;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * Lambda 语法使用 Wrapper
 * <p>统一处理解析 lambda 获取 column</p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public abstract class AbstractLambdaWrapper<T, Children extends AbstractLambdaWrapper<T, Children>>
        extends AbstractWrapper<T, SFunction<T, ?>, Children> {

    private Map<String, ColumnCache> columnMap = null;
    private boolean initColumnMap = false;

    @Override
    @SafeVarargs
    protected final String columnsToString(SFunction<T, ?>... columns) {
        return columnsToString(true, columns);
    }

    @SafeVarargs
    protected final String columnsToString(boolean onlyColumn, SFunction<T, ?>... columns) {
        return columnsToString(onlyColumn, CollUtil.toList(columns));
    }

    protected final String columnsToString(boolean onlyColumn, List<SFunction<T, ?>> columns) {
        return columns.stream().map(i -> columnToString(i, onlyColumn)).collect(joining(StringPool.COMMA));
    }

    @Override
    protected String columnToString(SFunction<T, ?> column) {
        return columnToString(column, true);
    }

    protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
        ColumnCache cache = getColumnCache(column);
        return onlyColumn ? cache.getColumn() : cache.getColumnSelect();
    }

    @Override
    @SafeVarargs
    public final Children groupBy(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.groupBy(condition, column, columns);
    }

    @Override
    @SafeVarargs
    public final Children orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return orderBy(condition, isAsc, column, CollUtil.toList(columns));
    }

    @Override
    @SafeVarargs
    public final Children groupBy(SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return doGroupBy(true, column, CollUtil.toList(columns));
    }


    @Override
    public Children groupBy(boolean condition, SFunction<T, ?> column, List<SFunction<T, ?>> columns) {
        return doGroupBy(condition,column,columns);
    }

    @Override
    @SafeVarargs
    public final Children orderByAsc(SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.orderByAsc(column, columns);
    }

    @Override
    @SafeVarargs
    public final Children orderByAsc(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.orderByAsc(condition, column, columns);
    }

    @Override
    @SafeVarargs
    public final Children orderByDesc(SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.orderByDesc(column, columns);
    }

    @Override
    @SafeVarargs
    public final Children orderByDesc(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.orderByDesc(condition, column, columns);
    }


    /**
     * 获取 SerializedLambda 对应的列信息，从 lambda 表达式中推测实体类
     * <p>
     * 如果获取不到列信息，那么本次条件组装将会失败
     *
     * @return 列
     */
    protected ColumnCache getColumnCache(SFunction<T, ?> column) {
        LambdaMeta meta = LambdaUtils.extract(column);
        String fieldName = PropertyNamer.methodToProperty(meta.getImplMethodName());
        Class<?> instantiatedClass = meta.getInstantiatedClass();
        tryInitCache(instantiatedClass);
        return getColumnCache(fieldName, instantiatedClass);
    }

    private void tryInitCache(Class<?> lambdaClass) {
        if (!initColumnMap) {
            final Class<T> entityClass = getEntityClass();
            if (entityClass != null) {
                lambdaClass = entityClass;
            }
            columnMap = LambdaUtils.getColumnMap(lambdaClass);
            Assert.notNull(columnMap, "can not find lambda cache for this entity [{}]", lambdaClass.getName());
            initColumnMap = true;
        }
    }

    private ColumnCache getColumnCache(String fieldName, Class<?> lambdaClass) {
        ColumnCache columnCache = columnMap.get(fieldName);
        Assert.notNull(columnCache, "can not find lambda cache for this property [{}] of entity [{}]",
                fieldName, lambdaClass.getName());
        return columnCache;
    }
}
