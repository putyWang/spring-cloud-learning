package com.learning.orm.core.parse.util;

import com.learning.orm.core.model.FieldModel;
import com.learning.orm.core.parse.item.expression.BinaryExpressionParse;
import com.learning.orm.core.parse.item.expression.CaseExpressionParse;
import com.learning.orm.core.parse.item.expression.ColumnParse;
import com.learning.orm.core.parse.item.expression.ParenthesisParse;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description 表达式解析工具类
 * @date 2024-09-29
 **/
public interface ExpressionParseUtil {

    /**
     * 表达式解析方法
     *
     * @param expression 表达式对象
     * @param alias 别名
     * @return 解析出的使用的字段列表
     */
    static List<FieldModel> parseExpression (Expression expression, String alias){
        // 1 expression 为空时直接返回空数组
        if (expression == null) {
            return new ArrayList<>();
        }
        // 2 按类型解析表达式
        if (expression instanceof Column) {
            return new ColumnParse().parse((Column) expression, alias);
        } else if (expression instanceof CaseExpression) {
            return new CaseExpressionParse().parse((CaseExpression) expression, alias);
        } else if (expression instanceof BinaryExpression) {
            return new BinaryExpressionParse().parse((BinaryExpression) expression, alias);
        } else if (expression instanceof Parenthesis) {
            return new ParenthesisParse().parse((Parenthesis) expression, alias);
        } else if (expression instanceof LongValue || expression instanceof StringValue) {
            return new ArrayList<>();
        } else {
            throw new RuntimeException("当前未实现该查询表达式解析:" + expression);
        }
    }
}
