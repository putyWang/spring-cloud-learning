package com.learning.orm.core.metadata.table;

import lombok.Data;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/8 下午6:08
 */
@Data
public class FieldMeta {

    /**
     * java 字段名
     */
    private String name;

    /**
     * 数据库字段名
     */
    private String column;

    /**
     * java 类型
     */
    private Class<?> javaType;
}
