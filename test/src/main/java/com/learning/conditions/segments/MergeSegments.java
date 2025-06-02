package com.learning.conditions.segments;

import com.learning.conditions.ISqlSegment;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:20
 */
public class MergeSegments implements ISqlSegment {
    private final NormalSegmentList normal = new NormalSegmentList();
    private final GroupBySegmentList groupBy = new GroupBySegmentList();
    private final HavingSegmentList having = new HavingSegmentList();
    private final OrderBySegmentList orderBy = new OrderBySegmentList();
    private String sqlSegment = "";
    private boolean cacheSqlSegment = true;

    public MergeSegments() {
    }

    public void add(ISqlSegment... iSqlSegments) {
        List<ISqlSegment> list = Arrays.asList(iSqlSegments);
        ISqlSegment firstSqlSegment = (ISqlSegment)list.get(0);
        if (MatchSegment.ORDER_BY.match(firstSqlSegment)) {
            this.orderBy.addAll(list);
        } else if (MatchSegment.GROUP_BY.match(firstSqlSegment)) {
            this.groupBy.addAll(list);
        } else if (MatchSegment.HAVING.match(firstSqlSegment)) {
            this.having.addAll(list);
        } else {
            this.normal.addAll(list);
        }

        this.cacheSqlSegment = false;
    }

    public String getSqlSegment() {
        if (this.cacheSqlSegment) {
            return this.sqlSegment;
        } else {
            this.cacheSqlSegment = true;
            if (this.normal.isEmpty()) {
                if (!this.groupBy.isEmpty() || !this.orderBy.isEmpty()) {
                    this.sqlSegment = this.groupBy.getSqlSegment() + this.having.getSqlSegment() + this.orderBy.getSqlSegment();
                }
            } else {
                this.sqlSegment = this.normal.getSqlSegment() + this.groupBy.getSqlSegment() + this.having.getSqlSegment() + this.orderBy.getSqlSegment();
            }

            return this.sqlSegment;
        }
    }

    public void clear() {
        this.sqlSegment = "";
        this.cacheSqlSegment = true;
        this.normal.clear();
        this.groupBy.clear();
        this.having.clear();
        this.orderBy.clear();
    }

    public NormalSegmentList getNormal() {
        return this.normal;
    }

    public GroupBySegmentList getGroupBy() {
        return this.groupBy;
    }

    public HavingSegmentList getHaving() {
        return this.having;
    }

    public OrderBySegmentList getOrderBy() {
        return this.orderBy;
    }
}
