package com.learning.orm.core.parse.item.expression;

import com.learning.orm.core.model.FieldModel;
import com.learning.orm.core.parse.util.ExpressionParseUtil;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.WhenClause;

import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description 纯字段表达式解析
 * @date 2024-09-29
 **/
public class CaseExpressionParse implements ExpressionParse<CaseExpression> {

    @Override
    public List<FieldModel> parse(CaseExpression expression, String alias) {
        // 1 解析 switch 表达式
        List<FieldModel> fieldList = ExpressionParseUtil.parseExpression(expression.getSwitchExpression(), "");
        // 2 解析 when 表达式
        for (WhenClause whenClause : expression.getWhenClauses()) {
            fieldList.addAll(ExpressionParseUtil.parseExpression(whenClause.getWhenExpression(), ""));
            fieldList.addAll(ExpressionParseUtil.parseExpression(whenClause.getThenExpression(), ""));
        }
        return fieldList;
    }
}
