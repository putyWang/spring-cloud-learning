package com.learning.orm.core.conditions.segments;

import com.learning.orm.core.conditions.ISqlSegment;
import com.learning.orm.core.constant.enums.SqlKeyword;
import com.learning.orm.core.constant.enums.WrapperKeyword;

import java.util.function.Predicate;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:26
 */
public enum MatchSegment {
    GROUP_BY((i) -> i == SqlKeyword.GROUP_BY),
    ORDER_BY((i) -> i == SqlKeyword.ORDER_BY),
    NOT((i) -> i == SqlKeyword.NOT),
    AND((i) -> i == SqlKeyword.AND),
    OR((i) -> i == SqlKeyword.OR),
    AND_OR((i) -> i == SqlKeyword.AND || i == SqlKeyword.OR),
    EXISTS((i) -> i == SqlKeyword.EXISTS),
    HAVING((i) -> i == SqlKeyword.HAVING),
    APPLY((i) -> i == WrapperKeyword.APPLY);

    private final Predicate<ISqlSegment> predicate;

    public boolean match(ISqlSegment segment) {
        return this.getPredicate().test(segment);
    }

    public Predicate<ISqlSegment> getPredicate() {
        return this.predicate;
    }

    private MatchSegment(final Predicate predicate) {
        this.predicate = predicate;
    }
}
