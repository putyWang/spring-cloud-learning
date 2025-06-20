package com.learning.orm.util.sql;


import com.learning.orm.core.constant.Constants;
import com.learning.orm.core.constant.enums.SqlLike;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * SqlUtils工具类
 * !!! 本工具不适用于本框架外的类使用 !!!
 *
 * @author Caratacus
 * @since 2016-11-13
 */
public abstract class SqlUtils implements Constants {

    /**
     * 用%连接like
     *
     * @param str 原字符串
     * @return like 的值
     */
    public static String concatLike(Object str, SqlLike type) {
        switch (type) {
            case LEFT:
                return PERCENT + str;
            case RIGHT:
                return str + PERCENT;
            default:
                return PERCENT + str + PERCENT;
        }
    }

    public static String buildWhere(Map<String, Object> columnMap) {
        return WHERE + SPACE + columnMap.entrySet().stream()
                .map(column -> {
                    if (SqlInjectionUtils.check(String.valueOf(column.getKey()))) {
                        throw new RuntimeException(column + "中存在 sql 关键字");
                    } if (column.getValue() instanceof String && SqlInjectionUtils.check(String.valueOf(column.getValue())) ) {
                        throw new RuntimeException(column.getValue() + "中存在 sql 关键字");
                    }
                    return column + EQUALS + column.getValue();
                }).collect(Collectors.joining(SPACE + AND + SPACE));
    }
}
