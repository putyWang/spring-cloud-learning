package com.learning.core.exception;

import com.learning.core.enums.ApiCode;

import java.util.Arrays;

public class SpringBootException
        extends RuntimeException {

    private static final long serialVersionUID = -2470461654663264392L;
    private Integer errorCode;
    private String message;
    private Object[] args;

    public SpringBootException() {
    }

    public SpringBootException(String message) {
        super(message);
        this.errorCode = ApiCode.SYSTEM_EXCEPTION.getCode();
        this.message = message;
    }

    public SpringBootException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public SpringBootException(Integer errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.args = args;
    }

    public SpringBootException(ApiCode apiCode) {
        super(apiCode.getMessage());
        this.errorCode = apiCode.getCode();
        this.message = apiCode.getMessage();
    }

    public SpringBootException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ApiCode.SYSTEM_EXCEPTION.getCode();
        this.message = message;
    }

    public SpringBootException(ApiCode apiCode, Throwable cause) {
        super(apiCode.getMessage(), cause);
        this.errorCode = apiCode.getCode();
        this.message = apiCode.getMessage();
    }

    public SpringBootException(Throwable cause) {
        super(cause);
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String toString() {
        return "SpringBootException(errorCode=" + this.getErrorCode() + ", message=" + this.getMessage() + ", args=" + Arrays.deepToString(this.getArgs()) + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SpringBootException)) {
            return false;
        } else {
            SpringBootException other = (SpringBootException) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                label41:
                {
                    Object this$errorCode = this.getErrorCode();
                    Object other$errorCode = other.getErrorCode();
                    if (this$errorCode == null) {
                        if (other$errorCode == null) {
                            break label41;
                        }
                    } else if (this$errorCode.equals(other$errorCode)) {
                        break label41;
                    }

                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                if (!Arrays.deepEquals(this.getArgs(), other.getArgs())) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SpringBootException;
    }

    public int hashCode() {
        int result = super.hashCode();
        Object errorCode = this.getErrorCode();
        result = result * 59 + (errorCode == null ? 43 : errorCode.hashCode());
        Object message = this.getMessage();
        result = result * 59 + (message == null ? 43 : message.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getArgs());
        return result;
    }
}
