package com.learning.conditions.segments;

import com.learning.conditions.ISqlSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:21
 */
public abstract class AbstractISegmentList extends ArrayList<ISqlSegment> implements ISqlSegment {

    ISqlSegment lastValue = null;
    boolean flushLastValue = false;
    private String sqlSegment = "";
    private boolean cacheSqlSegment = true;

    public AbstractISegmentList() {
    }

    public boolean addAll(Collection<? extends ISqlSegment> c) {
        List<ISqlSegment> list = new ArrayList(c);
        boolean goon = this.transformList(list, list.get(0), list.get(list.size() - 1));
        if (goon) {
            this.cacheSqlSegment = false;
            if (this.flushLastValue) {
                this.flushLastValue(list);
            }

            return super.addAll(list);
        } else {
            return false;
        }
    }

    protected abstract boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment);

    private void flushLastValue(List<ISqlSegment> list) {
        this.lastValue = list.get(list.size() - 1);
    }

    void removeAndFlushLast() {
        this.remove(this.size() - 1);
        this.flushLastValue(this);
    }

    public String getSqlSegment() {
        if (this.cacheSqlSegment) {
            return this.sqlSegment;
        } else {
            this.cacheSqlSegment = true;
            this.sqlSegment = this.childrenSqlSegment();
            return this.sqlSegment;
        }
    }

    protected abstract String childrenSqlSegment();

    public void clear() {
        super.clear();
        this.lastValue = null;
        this.sqlSegment = "";
        this.cacheSqlSegment = true;
    }
}
