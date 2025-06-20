package com.learning.orm.core.conditions;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午10:36
 */
@Data
public class SharedString implements Serializable {
    private static final long serialVersionUID = -1536422416594422874L;

    private String stringValue;

    public static SharedString emptyString() {
        return new SharedString("");
    }

    public void toEmpty() {
        this.stringValue = "";
    }

    public void toNull() {
        this.stringValue = null;
    }

    public SharedString(final String stringValue) {
        this.stringValue = stringValue;
    }

    public SharedString() {
    }
}

