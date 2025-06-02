package com.learning.conditions.segments;

import com.learning.conditions.ISqlSegment;
import com.learning.constant.enums.SqlKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:25
 */
public class HavingSegmentList extends AbstractISegmentList {
    public HavingSegmentList() {
    }

    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        if (!this.isEmpty()) {
            this.add(SqlKeyword.AND);
        }

        list.remove(0);
        return true;
    }

    protected String childrenSqlSegment() {
        return this.isEmpty() ? "" : this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(" ", " " + SqlKeyword.HAVING.getSqlSegment() + " ", ""));
    }
}
