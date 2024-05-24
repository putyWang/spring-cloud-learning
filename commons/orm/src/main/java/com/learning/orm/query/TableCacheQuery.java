package com.learning.orm.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: TableCacheQuery
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Setter
@Getter
public class TableCacheQuery extends PageQuery {
    private String rowId;
    private String name;
    private String dataBaseName;
}
