package com.learning.conditions.segments;

import com.learning.conditions.ISqlSegment;
import com.learning.constant.enums.SqlKeyword;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:25
 */
public class OrderBySegmentList extends AbstractISegmentList {
    public OrderBySegmentList() {
    }

    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        list.remove(0);
        List<ISqlSegment> sqlSegmentList = new ArrayList(list);
        list.clear();
        list.add(() -> sqlSegmentList.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(" ")));
        return true;
    }

    protected String childrenSqlSegment() {
        return this.isEmpty() ? "" : this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(",", " " + SqlKeyword.ORDER_BY.getSqlSegment() + " ", ""));
    }
}
