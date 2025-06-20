package com.learning.orm.core.conditions;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.learning.orm.core.conditions.segments.MergeSegments;
import com.learning.orm.core.conditions.segments.NormalSegmentList;
import com.learning.orm.core.constant.Constants;
import com.learning.orm.core.constant.StringPool;

import java.util.Objects;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:19
 */
/**
 * 条件构造抽象类
 *
 * @author hubin
 * @since 2018-05-25
 */
@SuppressWarnings("all")
public abstract class Wrapper<T> implements ISqlSegment {

    /**
     * 实体对象（子类实现）
     *
     * @return 泛型 T
     */
    public abstract T getEntity();

    public String getSqlSelect() {
        return null;
    }

    public String getSqlSet() {
        return null;
    }

    public String getSqlComment() {
        return null;
    }

    public String getSqlFirst() {
        return null;
    }

    /**
     * 获取 MergeSegments
     */
    public abstract MergeSegments getExpression();

    /**
     * 获取自定义SQL 简化自定义XML复杂情况
     * <p>
     * 使用方法: `select xxx from table` + ${ew.customSqlSegment}
     * <p>
     * 注意事项:
     * 1. 逻辑删除需要自己拼接条件 (之前自定义也同样)
     * 2. 不支持wrapper中附带实体的情况 (wrapper自带实体会更麻烦)
     * 3. 用法 ${ew.customSqlSegment} (不需要where标签包裹,切记!)
     * 4. ew是wrapper定义别名,不能使用其他的替换
     */
    public String getCustomSqlSegment() {
        MergeSegments expression = getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = getSqlSegment();
            if (StrUtil.isNotBlank(sqlSegment)) {
                if (normal.isEmpty()) {
                    return sqlSegment;
                } else {
                    return Constants.WHERE + StringPool.SPACE + sqlSegment;
                }
            }
        }
        return StringPool.EMPTY;
    }

    /**
     * 查询条件为空(包含entity)
     */
    public boolean isEmptyOfWhere() {
        return isEmptyOfNormal() && isEmptyOfEntity();
    }

    /**
     * 查询条件不为空(包含entity)
     */
    public boolean isNonEmptyOfWhere() {
        return !isEmptyOfWhere();
    }

    /**
     * 查询条件为空(不包含entity)
     */
    public boolean isEmptyOfNormal() {
        return CollUtil.isEmpty(getExpression().getNormal());
    }

    /**
     * 查询条件为空(不包含entity)
     */
    public boolean isNonEmptyOfNormal() {
        return !isEmptyOfNormal();
    }

    @Deprecated
    public boolean nonEmptyOfNormal() {
        return isNonEmptyOfNormal();
    }

    /**
     * 深层实体判断属性
     *
     * @return true 不为空
     */
    public boolean isNonEmptyOfEntity() {
        return getEntity() != null;
    }

    /**
     * 深层实体判断属性
     *
     * @return true 为空
     */
    public boolean isEmptyOfEntity() {
        return !isNonEmptyOfEntity();
    }

    /**
     * 获取格式化后的执行sql
     *
     * @return sql
     * @since 3.3.1
     */
    public String getTargetSql() {
        return getSqlSegment().replaceAll("#\\{.+?}", "?");
    }

    /**
     * 条件清空
     *
     * @since 3.3.1
     */
    abstract public void clear();
}
