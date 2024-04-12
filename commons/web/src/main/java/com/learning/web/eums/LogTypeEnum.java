package com.learning.web.eums;

import lombok.Getter;

/**
 * 记录日志类型
 */
@Getter
public enum LogTypeEnum {

    INSERT(1, "insert", "新值日志"),
    QUERY(2, "query", "查询日志"),
    UPDATE(3, "update", "更新日志"),
    DELETE(4, "delete", "删除日志");

    /**
     * 日志类型， 1： 操作日志， 2： 登录日志 3： 异常日志
     */
    private Integer type;

    /**
     * 类型值
     */
    private String value;

    /**
     * 类型简介
     */
    private String desc;

    LogTypeEnum(int type, String value, String desc) {
        this.type = type;
        this.value = value;
        this.desc = desc;
    }

    public Integer getType() {
        return this.type;
    }
}
