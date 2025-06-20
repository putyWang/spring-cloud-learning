package com.learning.orm.core.conditions;

import java.io.Serializable;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午6:18
 */
@FunctionalInterface
public interface ISqlSegment extends Serializable {
    String getSqlSegment();
}
