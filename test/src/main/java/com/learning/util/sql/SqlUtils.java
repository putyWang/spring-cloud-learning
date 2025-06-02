package com.learning.util.sql;

import com.learning.constant.enums.SqlLike;
import com.learning.constant.Constants;

import java.util.regex.Pattern;

/**
 * SqlUtils工具类
 * !!! 本工具不适用于本框架外的类使用 !!!
 *
 * @author Caratacus
 * @since 2016-11-13
 */
public abstract class SqlUtils implements Constants {
    private static final String tp = "[\\w-,]+?";
    private static final Pattern pattern = Pattern.compile(String.format("\\{@((%s)|(%s:\\w+?)|(%s:\\w+?:\\w+?))}", tp, tp, tp));

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
}
