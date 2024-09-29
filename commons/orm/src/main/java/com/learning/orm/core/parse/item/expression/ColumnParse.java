package com.learning.orm.core.parse.item.expression;

import com.learning.orm.core.model.FieldModel;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description 纯字段表达式解析
 * @date 2024-09-29
 **/
public class ColumnParse implements ExpressionParse<Column> {
    @Override
    public List<FieldModel> parse(Column column, String alias) {
        ArrayList<FieldModel> fieldList = new ArrayList<>();
        fieldList.add(
                new FieldModel()
                        .setColumnName(column.getColumnName())
                        .setTableName(column.getTable().getName())
        );
        return fieldList;
    }
}
