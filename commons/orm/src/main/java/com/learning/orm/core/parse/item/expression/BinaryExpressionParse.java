package com.learning.orm.core.parse.item.expression;

import com.learning.orm.core.model.FieldModel;
import com.learning.orm.core.parse.util.ExpressionParseUtil;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description 纯字段表达式解析
 * @date 2024-09-29
 **/
public class BinaryExpressionParse implements ExpressionParse<BinaryExpression> {

    @Override
    public List<FieldModel> parse(BinaryExpression expression, String alias) {
        // 1 解析左表达式
        List<FieldModel> fieldList = ExpressionParseUtil.parseExpression(expression.getLeftExpression(), "");
        // 2 解析右表达式
        fieldList.addAll(ExpressionParseUtil.parseExpression(expression.getRightExpression(), ""));
        return fieldList;
    }
}
