package com.learning.orm.core.parse.item.expression;

import com.learning.orm.core.model.FieldModel;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description 表达式节点解析
 * @date 2024-09-29
 **/
public interface ExpressionParse<T extends Expression> {

    List<FieldModel> parse(T t, String alias);
}
