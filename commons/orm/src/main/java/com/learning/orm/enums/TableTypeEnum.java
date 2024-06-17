package com.learning.orm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName: TableTypeEnum
 * @Description: 表类型枚举
 * @author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@AllArgsConstructor
public enum TableTypeEnum {
    /**
     * 不分库分表
     */
    SINGLE_TABLE(0),
    /**
     * 按年分表
     */
    SINGLE_LIBRARY_YEAR_TABLE(1),
    /**
     * 按月分表
     */
    SINGLE_LIBRARY_MONTH_TABLE(2),
    /**
     * 按年分库年分表
     */
    YEAR_LIBRARY_YEAR_TABLE(3),
    /**
     * 按年分库按月分表
     */
    YEAR_LIBRARY_MONTH_TABLE(4),
    /**
     *
     */
    SINGLE_LIBRARY_PARTITION(5),
    /**
     *
     */
    YEAR_LIBRARY_PARTITION(6),
    /**
     *
     */
    SEVEN(7),
    /**
     * 通用表
     */
    WARD_TABLE(8),
    /**
     *
     */
    NINE(9);

    @Getter
    private final Integer value;
}

