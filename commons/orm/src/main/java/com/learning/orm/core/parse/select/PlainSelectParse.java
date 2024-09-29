package com.learning.orm.core.parse.select;

import com.learning.orm.core.model.FieldModel;
import com.learning.orm.core.model.TableModel;
import com.learning.orm.core.parse.util.ExpressionParseUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description PlainSelect节点解析
 * @date 2024-09-29
 **/
public class PlainSelectParse implements SelectParse {

    private final PlainSelect selectNode;

    private final List<FieldModel> fieldList = new ArrayList<>();

    private final List<TableModel> TableList = new ArrayList<>();

    private final String alias;

    public PlainSelectParse (PlainSelect selectNode, String alias){
        this.selectNode = selectNode;
        this.alias = alias;
    }

    @Override
    public void parse() {
        // 1 对查询项目列表进行解析
        for (SelectItem<?> selectItem : selectNode.getSelectItems()) {
            fieldList.addAll(ExpressionParseUtil.parseExpression(selectItem.getExpression(), selectItem.getAlias().getName()));
        }
    }
}
