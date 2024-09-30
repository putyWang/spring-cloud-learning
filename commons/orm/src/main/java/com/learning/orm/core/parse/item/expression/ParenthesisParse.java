package com.learning.orm.core.parse.item.expression;

import com.learning.orm.core.model.FieldModel;
import com.learning.orm.core.parse.util.ExpressionParseUtil;
import net.sf.jsqlparser.expression.Parenthesis;

import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description 带括号的表达式解析
 * @date 2024-09-30
 **/
public class ParenthesisParse implements ExpressionParse<Parenthesis>{
    @Override
    public List<FieldModel> parse(Parenthesis parenthesis, String alias) {
        return ExpressionParseUtil.parseExpression(parenthesis.getExpression(), alias);
    }
}
