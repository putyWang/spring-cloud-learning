package com.learning.web.except;

import com.learning.core.domain.enums.ApiCode;

/**
 * 异常构造类
 */
public class ExceptionBuilder {

    private ExceptionBuilder() {
    }

    public static BaseException build(String message) {
        return new BaseException(ApiCode.SYSTEM_EXCEPTION.getCode(), message);
    }

    public static BaseException build(Integer errorCode, String message) {
        return new BaseException(errorCode, message);
    }

    public static BaseException build(Integer errorCode, String message, Object... args) {
        return new BaseException(errorCode, message, args);
    }
}
