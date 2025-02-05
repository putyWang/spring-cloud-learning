package exception;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class ValidationException extends RuntimeException {
    public int getCode() {
        return ResultStatus.FAILED_DATA_VALIDATE.getValue();
    }

    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String msg, Throwable e) {
        super(msg, e);
    }

    public ValidationException(String msg, int code) {
        super(msg, code);
    }

    public ValidationException(String msg, int code, Throwable e) {
        super(msg, code, e);
    }

    public ValidationException(String msg, Object data) {
        super(msg, data);
    }

    public ValidationException(String msg, Object data, Throwable e) {
        super(msg, data, e);
    }
}
