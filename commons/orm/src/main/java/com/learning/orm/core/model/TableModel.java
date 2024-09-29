package com.learning.orm.core.model;

import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.schema.Table;

/**
 * @author WangWei
 * @version v 1.0
 * @description 表名字段名映射对象
 * @date 2024-09-29
 **/
@Data
@Accessors(chain = true)
public class TableModel {

    private String tableAlia;

    private String tableName;

    private Table table;
}
