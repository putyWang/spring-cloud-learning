package com.learning.orm.core.conditions;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.learning.orm.core.conditions.interfaces.Compare;
import com.learning.orm.core.conditions.interfaces.Func;
import com.learning.orm.core.conditions.interfaces.Join;
import com.learning.orm.core.conditions.interfaces.Nested;
import com.learning.orm.core.conditions.segments.ColumnSegment;
import com.learning.orm.core.conditions.segments.MergeSegments;
import com.learning.orm.core.constant.Constants;
import com.learning.orm.core.constant.StringPool;
import com.learning.orm.core.constant.enums.SqlKeyword;
import com.learning.orm.core.constant.enums.SqlLike;
import com.learning.orm.core.constant.enums.WrapperKeyword;
import com.learning.orm.util.SqlScriptUtils;
import com.learning.orm.util.sql.SqlUtils;
import lombok.Getter;
import org.apache.commons.lang.SerializationUtils;
import cn.hutool.core.lang.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午10:35
 */
@SuppressWarnings({"unchecked"})
public abstract class AbstractWrapper<T, R, Children extends AbstractWrapper<T, R, Children>>
        extends Wrapper<T>
        implements Compare<Children, R>, Nested<Children, Children>, Join<Children>, Func<Children, R> {

    /**
     * 占位符
     */
    protected final Children typedThis = (Children) this;
    /**
     * 必要度量
     */
    protected AtomicInteger paramNameSeq;
    @Getter
    protected Map<String, Object> paramNameValuePairs;
    /**
     * 其他
     */
    protected SharedString paramAlias;
    protected SharedString lastSql;
    /**
     * SQL注释
     */
    protected SharedString sqlComment;
    /**
     * SQL起始语句
     */
    protected SharedString sqlFirst;
    /**
     * 数据库表映射实体类
     */
    private T entity;
    protected MergeSegments expression;
    /**
     * 实体类型(主要用于确定泛型以及取TableInfo缓存)
     */
    private Class<T> entityClass;

    @Override
    public T getEntity() {
        return entity;
    }

    public Children setEntity(T entity) {
        this.entity = entity;
        return typedThis;
    }

    public Class<T> getEntityClass() {
        if (entityClass == null && entity != null) {
            entityClass = (Class<T>) entity.getClass();
        }
        return entityClass;
    }

    public Children setEntityClass(Class<T> entityClass) {
        if (entityClass != null) {
            this.entityClass = entityClass;
        }
        return typedThis;
    }

    @Override
    public <V> Children allEq(boolean condition, Map<R, V> params, boolean null2IsNull) {
        if (condition && CollUtil.isNotEmpty(params)) {
            params.forEach((k, v) -> {
                if (! StrUtil.isBlankIfStr(v)) {
                    eq(k, v);
                } else {
                    if (null2IsNull) {
                        isNull(k);
                    }
                }
            });
        }
        return typedThis;
    }

    @Override
    public <V> Children allEq(boolean condition, BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
        if (condition && CollUtil.isNotEmpty(params)) {
            params.forEach((k, v) -> {
                if (filter.test(k, v)) {
                    if (! StrUtil.isBlankIfStr(v)) {
                        eq(k, v);
                    } else {
                        if (null2IsNull) {
                            isNull(k);
                        }
                    }
                }
            });
        }
        return typedThis;
    }

    @Override
    public Children eq(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.EQ, val);
    }

    @Override
    public Children ne(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.NE, val);
    }

    @Override
    public Children gt(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.GT, val);
    }

    @Override
    public Children ge(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.GE, val);
    }

    @Override
    public Children lt(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.LT, val);
    }

    @Override
    public Children le(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.LE, val);
    }

    @Override
    public Children like(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.LIKE, column, val, SqlLike.DEFAULT);
    }

    @Override
    public Children notLike(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.NOT_LIKE, column, val, SqlLike.DEFAULT);
    }

    @Override
    public Children likeLeft(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.LIKE, column, val, SqlLike.LEFT);
    }

    @Override
    public Children likeRight(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.LIKE, column, val, SqlLike.RIGHT);
    }

    @Override
    public Children notLikeLeft(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.NOT_LIKE, column, val, SqlLike.LEFT);
    }

    @Override
    public Children notLikeRight(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.NOT_LIKE, column, val, SqlLike.RIGHT);
    }

    @Override
    public Children between(boolean condition, R column, Object val1, Object val2) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.BETWEEN,
                () -> formatParam(null, val1), SqlKeyword.AND, () -> formatParam(null, val2)));
    }

    @Override
    public Children notBetween(boolean condition, R column, Object val1, Object val2) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.NOT_BETWEEN,
                () -> formatParam(null, val1), SqlKeyword.AND, () -> formatParam(null, val2)));
    }

    @Override
    public Children and(boolean condition, Consumer<Children> consumer) {
        return and(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children or(boolean condition, Consumer<Children> consumer) {
        return or(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children nested(boolean condition, Consumer<Children> consumer) {
        return addNestedCondition(condition, consumer);
    }

    @Override
    public Children not(boolean condition, Consumer<Children> consumer) {
        return not(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children or(boolean condition) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.OR));
    }

    @Override
    public Children apply(boolean condition, String applySql, Object... values) {
        return maybeDo(condition, () -> appendSqlSegments(WrapperKeyword.APPLY, () -> formatSqlMaybeWithParam(applySql, values)));
    }

    @Override
    public Children last(boolean condition, String lastSql) {
        if (condition) {
            this.lastSql.setStringValue(StringPool.SPACE + lastSql);
        }
        return typedThis;
    }

    @Override
    public Children comment(boolean condition, String comment) {
        if (condition) {
            this.sqlComment.setStringValue(comment);
        }
        return typedThis;
    }

    @Override
    public Children first(boolean condition, String firstSql) {
        if (condition) {
            this.sqlFirst.setStringValue(firstSql);
        }
        return typedThis;
    }

    @Override
    public Children exists(boolean condition, String existsSql, Object... values) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.EXISTS,
                () -> String.format("(%s)", formatSqlMaybeWithParam(existsSql, values))));
    }

    @Override
    public Children notExists(boolean condition, String existsSql, Object... values) {
        return not(condition).exists(condition, existsSql, values);
    }

    @Override
    public Children isNull(boolean condition, R column) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IS_NULL));
    }

    @Override
    public Children isNotNull(boolean condition, R column) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IS_NOT_NULL));
    }

    @Override
    public Children in(boolean condition, R column, Collection<?> coll) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IN, inExpression(coll)));
    }

    @Override
    public Children in(boolean condition, R column, Object... values) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IN, inExpression(values)));
    }

    @Override
    public Children notIn(boolean condition, R column, Collection<?> coll) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.NOT_IN, inExpression(coll)));
    }

    @Override
    public Children notIn(boolean condition, R column, Object... values) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.NOT_IN, inExpression(values)));
    }

    @Override
    public Children inSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IN,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children gtSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.GT,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children geSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.GE,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children ltSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.LT,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children leSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.LE,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children notInSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.NOT_IN,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children groupBy(boolean condition, R column, R... columns) {
        return doGroupBy(condition, column, CollUtil.toList(columns));
    }

    @Override
    public Children groupBy(boolean condition, R column, List<R> columns) {
        return doGroupBy(condition, column, columns);
    }

    public Children doGroupBy(boolean condition, R column, List<R> columns) {
        return maybeDo(condition, () -> {
            String one = StringPool.EMPTY;
            if (column != null) {
                one = columnToString(column);
            }
            if (CollUtil.isNotEmpty(columns)) {
                one += column != null ? StringPool.COMMA + columnsToString(columns) : columnsToString(columns);
            }
            final String finalOne = one;
            appendSqlSegments(SqlKeyword.GROUP_BY, () -> finalOne);
        });
    }


    public Children doOrderBy(boolean condition, boolean isAsc, R column, List<R> columns) {
        return maybeDo(condition, () -> {
            final SqlKeyword mode = isAsc ? SqlKeyword.ASC : SqlKeyword.DESC;
            if (column != null) {
                appendSqlSegments(SqlKeyword.ORDER_BY, columnToSqlSegment(column), mode);
            }
            if (CollUtil.isNotEmpty(columns)) {
                columns.forEach(c -> appendSqlSegments(SqlKeyword.ORDER_BY,
                        columnToSqlSegment(c), mode));
            }
        });
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R column, R... columns) {
        return doOrderBy(condition, isAsc, column, CollUtil.toList(columns));
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R column, List<R> columns) {
        return doOrderBy(condition, isAsc, column, columns);
    }

    @Override
    public Children groupBy(boolean condition, R column) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.GROUP_BY, () -> columnToString(column)));
    }

    @Override
    public Children groupBy(boolean condition, List<R> columns) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.GROUP_BY, () -> columnsToString(columns)));
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R column) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.ORDER_BY, columnToSqlSegment(column),
                isAsc ? SqlKeyword.ASC : SqlKeyword.DESC));
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, List<R> columns) {
        return maybeDo(condition, () -> columns.forEach(c -> appendSqlSegments(SqlKeyword.ORDER_BY,
                columnToSqlSegment(c), isAsc ? SqlKeyword.ASC : SqlKeyword.DESC)));
    }

    @Override
    public Children having(boolean condition, String sqlHaving, Object... params) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.HAVING, () -> formatSqlMaybeWithParam(sqlHaving, params)));
    }

    @Override
    public Children func(boolean condition, Consumer<Children> consumer) {
        return maybeDo(condition, () -> consumer.accept(typedThis));
    }

    /**
     * 内部自用
     * <p>NOT 关键词</p>
     */
    protected Children not(boolean condition) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.NOT));
    }

    /**
     * 内部自用
     * <p>拼接 AND</p>
     */
    protected Children and(boolean condition) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.AND));
    }

    /**
     * 内部自用
     * <p>拼接 LIKE 以及 值</p>
     */
    protected Children likeValue(boolean condition, SqlKeyword keyword, R column, Object val, SqlLike sqlLike) {
        return maybeDo(
                condition,
                () -> appendSqlSegments(columnToSqlSegment(column), keyword,
                () -> formatParam(null, SqlUtils.concatLike(val, sqlLike)))
        );
    }

    /**
     * 普通查询条件
     *
     * @param condition  是否执行
     * @param column     属性
     * @param sqlKeyword SQL 关键词
     * @param val        条件值
     */
    protected Children addCondition(boolean condition, R column, SqlKeyword sqlKeyword, Object val) {
        return maybeDo(
                condition,
                () -> appendSqlSegments(
                        columnToSqlSegment(column),
                        sqlKeyword,
                        () -> formatParam(null, val)
                )
        );
    }

    /**
     * 多重嵌套查询条件
     *
     * @param condition 查询条件值
     */
    protected Children addNestedCondition(boolean condition, Consumer<Children> consumer) {
        return maybeDo(condition, () -> {
            final Children instance = instance();
            consumer.accept(instance);
            appendSqlSegments(WrapperKeyword.APPLY, instance);
        });
    }

    /**
     * 子类返回一个自己的新对象
     */
    protected abstract Children instance();

    /**
     * 格式化 sql
     * <p>
     * 支持 "{0}" 这种,或者 "sql {0} sql" 这种
     * 也支持 "sql {0,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler} sql" 这种
     *
     * @param sqlStr 可能是sql片段
     * @param params 参数
     * @return sql片段
     */
    @SuppressWarnings("SameParameterValue")
    protected final String formatSqlMaybeWithParam(String sqlStr, Object... params) {
        if (StrUtil.isBlank(sqlStr)) {
            return null;
        }
        if (ArrayUtil.isNotEmpty(params)) {
            for (int i = 0; i < params.length; ++i) {
                String target = Constants.LEFT_BRACE + i + Constants.RIGHT_BRACE;
                if (sqlStr.contains(target)) {
                    sqlStr = sqlStr.replace(target, formatParam(null, params[i]));
                } else {
                    Matcher matcher = Pattern.compile("[{]" + i + ",[a-zA-Z0-9.,=]+}").matcher(sqlStr);
                    if (!matcher.find()) {
                        throw new RuntimeException(String.format("Please check the syntax correctness! sql not contains: \"%s\"", target));
                    }
                    String group = matcher.group();
                    sqlStr = sqlStr.replace(group, formatParam(group.substring(target.length(), group.length() - 1), params[i]));
                }
            }
        }
        return sqlStr;
    }

    /**
     * 处理入参
     *
     * @param mapping 例如: "javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler" 这种
     * @param param   参数
     * @return value
     */
    protected final String formatParam(String mapping, Object param) {
        final String genParamName = Constants.WRAPPER_PARAM + paramNameSeq.incrementAndGet();
        final String paramStr = getParamAlias() + Constants.WRAPPER_PARAM_MIDDLE + genParamName;
        paramNameValuePairs.put(genParamName, param);
        return SqlScriptUtils.safeParam(paramStr, mapping);
    }

    /**
     * 函数化的做事
     *
     * @param condition 做不做
     * @param something 做什么
     * @return Children
     */
    protected final Children maybeDo(boolean condition, DoSomething something) {
        if (condition) {
            something.doIt();
        }
        return typedThis;
    }

    /**
     * 获取in表达式 包含括号
     *
     * @param value 集合
     */
    protected ISqlSegment inExpression(Collection<?> value) {
        if (CollUtil.isEmpty(value)) {
            return () -> "()";
        }
        return () -> value.stream().map(i -> formatParam(null, i))
                .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }

    /**
     * 获取in表达式 包含括号
     *
     * @param values 数组
     */
    protected ISqlSegment inExpression(Object[] values) {
        if (ArrayUtil.isEmpty(values)) {
            return () -> "()";
        }
        return () -> Arrays.stream(values).map(i -> formatParam(null, i))
                .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }

    /**
     * 必要的初始化
     */
    protected void initNeed() {
        paramNameSeq = new AtomicInteger(0);
        paramNameValuePairs = new HashMap<>(16);
        expression = new MergeSegments();
        lastSql = SharedString.emptyString();
        sqlComment = SharedString.emptyString();
        sqlFirst = SharedString.emptyString();
    }

    @Override
    public void clear() {
        entity = null;
        paramNameSeq.set(0);
        paramNameValuePairs.clear();
        expression.clear();
        lastSql.toEmpty();
        sqlComment.toEmpty();
        sqlFirst.toEmpty();
    }

    /**
     * 添加 where 片段
     *
     * @param sqlSegments ISqlSegment 数组
     */
    protected void appendSqlSegments(ISqlSegment... sqlSegments) {
        expression.add(sqlSegments);
    }

    /**
     * 是否使用默认注解
     *
     * @return true 使用 false 不使用
     */
    public boolean isUseAnnotationOrderBy() {
        final String _sqlSegment = this.getSqlSegment();
        if (StrUtil.isBlank(_sqlSegment)) {
            return true;
        }
        final String _sqlSegmentUpper = _sqlSegment.toUpperCase();
        return !(_sqlSegmentUpper.contains(Constants.ORDER_BY) || _sqlSegmentUpper.contains(Constants.LIMIT));
    }

    @Override
    public String getSqlSegment() {
        return expression.getSqlSegment() + lastSql.getStringValue();
    }

    @Override
    public String getSqlComment() {
        if (StrUtil.isNotBlank(sqlComment.getStringValue())) {
            return "/*" + sqlComment.getStringValue() + "*/";
        }
        return null;
    }

    @Override
    public String getSqlFirst() {
        if (StrUtil.isNotBlank(sqlFirst.getStringValue())) {
            return sqlFirst.getStringValue();
        }
        return null;
    }

    @Override
    public MergeSegments getExpression() {
        return expression;
    }

    public String getParamAlias() {
        return paramAlias == null ? Constants.WRAPPER : paramAlias.getStringValue();
    }

    /**
     * 参数别名设置，初始化时优先设置该值、重复设置异常
     *
     * @param paramAlias 参数别名
     * @return Children
     */
    @SuppressWarnings("unused")
    public Children setParamAlias(String paramAlias) {
        Assert.notEmpty(paramAlias, "paramAlias can not be empty!");
        Assert.isNull(paramNameValuePairs, "Please call this method before working!");
        Assert.isNull(this.paramAlias, "Please do not call the method repeatedly!");
        this.paramAlias = new SharedString(paramAlias);
        return typedThis;
    }

    /**
     * 获取 columnName
     */
    protected final ColumnSegment columnToSqlSegment(R column) {
        return () -> columnToString(column);
    }

    /**
     * 获取 columnName
     */
    protected String columnToString(R column) {
        return (String) column;
    }

    /**
     * 获取 columnNames
     */
    protected String columnsToString(R... columns) {
        return Arrays.stream(columns).map(this::columnToString).collect(joining(StringPool.COMMA));
    }

    /**
     * 多字段转换为逗号 "," 分割字符串
     *
     * @param columns 多字段
     */
    protected String columnsToString(List<R> columns) {
        return columns.stream().map(this::columnToString).collect(joining(StringPool.COMMA));
    }

    @Override
    @SuppressWarnings("all")
    public Children clone() {
        return (Children) SerializationUtils.clone(typedThis);
    }

    /**
     * 做事函数
     */
    @FunctionalInterface
    public interface DoSomething {

        void doIt();
    }
}
