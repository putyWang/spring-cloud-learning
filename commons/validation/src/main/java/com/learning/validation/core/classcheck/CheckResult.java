package com.learning.validation.core.classcheck;

import lombok.Data;

/**
 * @author WangWei
 * @version v 1.0
 * @description 自定义类检查器结果
 * @date 2024-06-21
 **/
@Data
public class CheckResult {
    /**
     * 是否验证成功
     */
    private boolean validate;

    /**
     * 验证结果消息
     */
    private String msg;

    /**
     *
     */
    private String failPty;

    public CheckResult(boolean validate) {
        this.validate = validate;
    }

    public CheckResult(boolean validate, String msg) {
        this.validate = validate;
        this.msg = msg;
    }

    public CheckResult(boolean validate, String failPty, String msg) {
        this.validate = validate;
        this.failPty = failPty;
        this.msg = msg;
    }

    public static CheckResult success() {
        return new CheckResult(true);
    }

    public static CheckResult fail(String msg) {
        return new CheckResult(false, msg);
    }

    public static CheckResult fail(String failPty, String msg) {
        return new CheckResult(false, failPty, msg);
    }
}