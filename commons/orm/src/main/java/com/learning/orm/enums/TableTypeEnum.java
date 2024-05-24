package com.learning.orm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName: TableTypeEnum
 * @Description:
 * @author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@AllArgsConstructor
public enum TableTypeEnum {
    /**
     *
     */
    SINGLE_TABLE(0),
    /**
     *
     */
    SINGLE_LIBRARY_YEAR_TABLE(1),
    /**
     *
     */
    SINGLE_LIBRARY_MONTH_TABLE(2),
    /**
     *
     */
    YEAR_LIBRARY_YEAR_TABLE(3),
    /**
     *
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
     *
     */
    WARD_TABLE(8),
    /**
     *
     */
    NINE(9);

    @Getter
    private final Integer value;
}

