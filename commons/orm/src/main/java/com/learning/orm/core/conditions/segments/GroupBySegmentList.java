package com.learning.orm.core.conditions.segments;

import com.learning.orm.core.conditions.ISqlSegment;
import com.learning.orm.core.constant.enums.SqlKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:23
 */
public class GroupBySegmentList extends AbstractISegmentList {
    public GroupBySegmentList() {
    }

    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        list.remove(0);
        return true;
    }

    protected String childrenSqlSegment() {
        return this.isEmpty() ? "" : (String)this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(",", " " + SqlKeyword.GROUP_BY.getSqlSegment() + " ", ""));
    }
}
