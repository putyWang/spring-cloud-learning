package com.learning.orm.core.parse.item.from;

import net.sf.jsqlparser.statement.select.FromItem;

/**
 * @author WangWei
 * @version v 1.0
 * @description from节点解析
 * @date 2024-09-29
 **/
public interface FromItemParse<T extends FromItem> {

    void parse(T t);
}
