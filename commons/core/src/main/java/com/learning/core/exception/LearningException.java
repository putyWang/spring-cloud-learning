package com.learning.core.exception;

import com.learning.core.enums.ApiCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * @author WangWei
 * @version v 1.0
 * @description 项目根异常
 * @date 2024-06-21
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LearningException
        extends RuntimeException {

    private static final long serialVersionUID = -2470461654663264392L;
    private Integer errorCode;
    private String message;
    private Object[] args;

    public LearningException(String message) {
        super(message);
        this.errorCode = ApiCode.SYSTEM_EXCEPTION.getCode();
        this.message = message;
    }

    public LearningException(String message,  Object... args) {
        this(ApiCode.SYSTEM_EXCEPTION.getCode(), message, args);
    }

    public LearningException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public LearningException(Integer errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.args = args;
    }

    public LearningException(ApiCode apiCode) {
        super(apiCode.getMessage());
        this.errorCode = apiCode.getCode();
        this.message = apiCode.getMessage();
    }

    public LearningException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ApiCode.SYSTEM_EXCEPTION.getCode();
        this.message = message;
    }

    public LearningException(ApiCode apiCode, Throwable cause) {
        super(apiCode.getMessage(), cause);
        this.errorCode = apiCode.getCode();
        this.message = apiCode.getMessage();
    }

    public LearningException(Throwable cause) {
        super(cause);
    }
}
