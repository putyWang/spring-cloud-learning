package com.learning.orm.core.metadata;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午10:50
 */
@Data
public class ColumnCache implements Serializable {
    private static final long serialVersionUID = -4586291538088403456L;
    private String column;
    private String columnSelect;

    public ColumnCache(final String column, final String columnSelect) {
        this.column = column;
        this.columnSelect = columnSelect;
    }
}

