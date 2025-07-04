package com.learning.orm.core.conditions.update;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.learning.orm.core.conditions.AbstractWrapper;
import com.learning.orm.core.conditions.SharedString;
import com.learning.orm.core.conditions.segments.MergeSegments;
import com.learning.orm.core.constant.Constants;
import com.learning.orm.util.sql.SqlInjectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Update 条件封装
 *
 * @author hubin miemie HCL
 * @since 2018-05-30
 */
public class UpdateWrapper<T> extends AbstractWrapper<T, String, UpdateWrapper<T>>
    implements Update<UpdateWrapper<T>, String> {

    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private final List<String> sqlSet;

    public UpdateWrapper() {
        // 如果无参构造函数，请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this(null);
    }

    public UpdateWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
        this.sqlSet = new ArrayList<>();
    }

    private UpdateWrapper(T entity, List<String> sqlSet, AtomicInteger paramNameSeq,
                          Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                          SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.paramAlias = paramAlias;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }


    /**
     * 检查 SQL 注入过滤
     */
    private boolean checkSqlInjection;

    /**
     * 开启检查 SQL 注入
     */
    public UpdateWrapper<T> checkSqlInjection() {
        this.checkSqlInjection = true;
        return this;
    }

    @Override
    protected String columnToString(String column) {
        if (checkSqlInjection && SqlInjectionUtils.check(column)) {
            throw new RuntimeException("Discovering SQL injection column: " + column);
        }
        return column;
    }

    @Override
    public String getSqlSet() {
        if (CollUtil.isEmpty(sqlSet)) {
            return null;
        }
        return String.join(Constants.COMMA, sqlSet);
    }

    @Override
    public UpdateWrapper<T> set(boolean condition, String column, Object val, String mapping) {
        return maybeDo(condition, () -> {
            String sql = formatParam(mapping, val);
            sqlSet.add(column + Constants.EQUALS + sql);
        });
    }

    @Override
    public UpdateWrapper<T> setSql(boolean condition, String setSql, Object... params) {
        return maybeDo(condition && StrUtil.isNotBlank(setSql), () -> {
            sqlSet.add(formatSqlMaybeWithParam(setSql, params));
        });
    }

    @Override
    public UpdateWrapper<T> setIncrBy(boolean condition, String column, Number val) {
        return maybeDo(condition, () -> {
            sqlSet.add(String.format("%s=%s + %s", column, column, val instanceof BigDecimal ? ((BigDecimal) val).toPlainString() : val));
        });
    }

    @Override
    public UpdateWrapper<T> setDecrBy(boolean condition, String column, Number val) {
        return maybeDo(condition, () -> {
            sqlSet.add(String.format("%s=%s - %s", column, column, val instanceof BigDecimal ? ((BigDecimal) val).toPlainString() : val));
        });
    }

    /**
     * 返回一个支持 lambda 函数写法的 wrapper
     */
    public LambdaUpdateWrapper<T> lambda() {
        return new LambdaUpdateWrapper<>(getEntity(), getEntityClass(), sqlSet, paramNameSeq, paramNameValuePairs,
            expression, paramAlias, lastSql, sqlComment, sqlFirst);
    }

    @Override
    protected UpdateWrapper<T> instance() {
        return new UpdateWrapper<>(getEntity(), null, paramNameSeq, paramNameValuePairs, new MergeSegments(),
            paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSet.clear();
    }
}
