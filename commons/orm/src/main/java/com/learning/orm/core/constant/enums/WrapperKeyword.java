package com.learning.orm.core.constant.enums;

import com.learning.orm.core.conditions.ISqlSegment;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:27
 */
public enum WrapperKeyword implements ISqlSegment {
    APPLY(null);

    private final String keyword;

    public String getSqlSegment() {
        return this.keyword;
    }

    WrapperKeyword(final String keyword) {
        this.keyword = keyword;
    }
}
