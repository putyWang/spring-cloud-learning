package com.learning.orm.core.constant.enums;

import com.learning.orm.core.conditions.ISqlSegment;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:24
 */
public enum SqlKeyword implements ISqlSegment {
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    ASC("ASC"),
    DESC("DESC");

    private final String keyword;

    public String getSqlSegment() {
        return this.keyword;
    }

    SqlKeyword(final String keyword) {
        this.keyword = keyword;
    }
}
