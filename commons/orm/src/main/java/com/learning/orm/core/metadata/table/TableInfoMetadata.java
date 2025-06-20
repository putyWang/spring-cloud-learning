package com.learning.orm.core.metadata.table;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/8 下午6:16
 */
@Data
public class TableInfoMetadata {

    public static final TableInfoMetadata EMPTY_INFO = new TableInfoMetadata();

    /**
     * 表名
     */
    private String name;

    /**
     * 主键信息
     */
    private FieldMeta idInfo;

    /**
     * 字段列表
     */
    private List<FieldMeta> fieldList = new ArrayList<>();
}
