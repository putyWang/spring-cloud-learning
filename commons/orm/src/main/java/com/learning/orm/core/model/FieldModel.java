package com.learning.orm.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author WangWei
 * @version v 1.0
 * @description 字段解析模块
 * @date 2024-09-29
 **/
@Data
@Accessors(chain = true)
public class FieldModel {

    private String alias;

    private String tableName;

    private String columnName;

    private Object value;
}
