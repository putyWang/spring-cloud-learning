package com.learning.orm.core.conditions.segments;

import com.learning.orm.core.conditions.ISqlSegment;
import com.learning.orm.core.constant.enums.SqlKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:21
 */
public class NormalSegmentList extends AbstractISegmentList {
    private boolean executeNot = true;

    NormalSegmentList() {
        this.flushLastValue = true;
    }

    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        if (list.size() == 1) {
            if (MatchSegment.NOT.match(firstSegment)) {
                this.executeNot = false;
                return false;
            }

            if (this.isEmpty()) {
                return false;
            }

            boolean matchLastAnd = MatchSegment.AND.match(this.lastValue);
            boolean matchLastOr = MatchSegment.OR.match(this.lastValue);
            if (matchLastAnd || matchLastOr) {
                if (matchLastAnd && MatchSegment.AND.match(firstSegment)) {
                    return false;
                }

                if (matchLastOr && MatchSegment.OR.match(firstSegment)) {
                    return false;
                }

                this.removeAndFlushLast();
            }
        } else {
            if (MatchSegment.APPLY.match(firstSegment)) {
                list.remove(0);
            }

            if (!MatchSegment.AND_OR.match(this.lastValue) && !this.isEmpty()) {
                this.add(SqlKeyword.AND);
            }

            if (!this.executeNot) {
                list.add(0, SqlKeyword.NOT);
                this.executeNot = true;
            }
        }

        return true;
    }

    protected String childrenSqlSegment() {
        if (MatchSegment.AND_OR.match(this.lastValue)) {
            this.removeAndFlushLast();
        }

        return "(" + this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(" ")) + ")";
    }

    public void clear() {
        super.clear();
        this.flushLastValue = true;
        this.executeNot = true;
    }
}
