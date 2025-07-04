package com.learning.orm.core.conditions.update;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.learning.orm.core.conditions.AbstractLambdaWrapper;
import com.learning.orm.core.conditions.SharedString;
import com.learning.orm.core.conditions.interfaces.SFunction;
import com.learning.orm.core.conditions.segments.MergeSegments;
import com.learning.orm.core.constant.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lambda 更新封装
 *
 * @author hubin miemie HCL
 * @since 2018-05-30
 */
public class LambdaUpdateWrapper<T> extends AbstractLambdaWrapper<T, LambdaUpdateWrapper<T>>
    implements Update<LambdaUpdateWrapper<T>, SFunction<T, ?>> {

    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private final List<String> sqlSet;

    public LambdaUpdateWrapper() {
        // 如果无参构造函数，请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this((T) null);
    }

    public LambdaUpdateWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
        this.sqlSet = new ArrayList<>();
    }

    public LambdaUpdateWrapper(Class<T> entityClass) {
        super.setEntityClass(entityClass);
        super.initNeed();
        this.sqlSet = new ArrayList<>();
    }

    LambdaUpdateWrapper(T entity, Class<T> entityClass, List<String> sqlSet, AtomicInteger paramNameSeq,
                        Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                        SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.paramAlias = paramAlias;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    @Override
    public LambdaUpdateWrapper<T> set(boolean condition, SFunction<T, ?> column, Object val, String mapping) {
        return maybeDo(condition, () -> {
            String sql = formatParam(mapping, val);
            sqlSet.add(columnToString(column) + Constants.EQUALS + sql);
        });
    }

    @Override
    public LambdaUpdateWrapper<T> setSql(boolean condition, String setSql, Object... params) {
        return maybeDo(condition && StrUtil.isNotBlank(setSql), () -> {
            sqlSet.add(formatSqlMaybeWithParam(setSql, params));
        });
    }

    @Override
    public LambdaUpdateWrapper<T> setIncrBy(boolean condition, SFunction<T, ?> column, Number val) {
        return maybeDo(condition, () -> {
            String realColumn = columnToString(column);
            sqlSet.add(String.format("%s=%s + %s", realColumn, realColumn, val instanceof BigDecimal ? ((BigDecimal) val).toPlainString() : val));
        });
    }

    @Override
    public LambdaUpdateWrapper<T> setDecrBy(boolean condition, SFunction<T, ?> column, Number val) {
        return maybeDo(condition, () -> {
            String realColumn = columnToString(column);
            sqlSet.add(String.format("%s=%s - %s", realColumn, realColumn, val instanceof BigDecimal ? ((BigDecimal) val).toPlainString() : val));
        });
    }

    @Override
    public String getSqlSet() {
        if (CollUtil.isEmpty(sqlSet)) {
            return null;
        }
        return String.join(Constants.COMMA, sqlSet);
    }

    @Override
    protected LambdaUpdateWrapper<T> instance() {
        return new LambdaUpdateWrapper<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
            new MergeSegments(), paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSet.clear();
    }
}
