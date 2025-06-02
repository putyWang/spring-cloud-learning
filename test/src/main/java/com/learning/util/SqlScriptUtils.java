package com.learning.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.JdbcType;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午10:40
 */
public class SqlScriptUtils {

    private SqlScriptUtils(){}

    public static String safeParam(final String param, final String mapping) {
        String target = "#{" + param;
        return StrUtil.isBlank(mapping) ? target + "}" : target + "," + mapping + "}";
    }
}

