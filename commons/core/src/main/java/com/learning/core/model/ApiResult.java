package com.learning.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.core.enums.ApiCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 8004487252556526569L;

    /**
     * 返回编码
     */
    private int code;

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 返回信息
     */
    private String message;
    private Object[] args;

    /**
     * 数据
     */
    private T data;

    @JSONField(
            format = "yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date time;

    public ApiResult() {
        this.time = new Date();
    }

    public ApiResult(int code, boolean success, String message, Object[] args, T data, Date time) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.args = args;
        this.data = data;
        this.time = time;
    }

    public static ApiResult<Boolean> result(boolean flag) {
        return flag ? ok() : fail();
    }

    public static ApiResult<Boolean> result(ApiCode apiCode) {
        return result(apiCode, null);
    }

    public static <T> ApiResult<T> result(ApiCode apiCode, T data) {
        return result(apiCode, apiCode.getMessage(), data);
    }

    public static <T> ApiResult<T> result(ApiCode apiCode, String message, T data) {
        boolean success = false;
        if (apiCode.getCode() == ApiCode.SUCCESS.getCode()) {
            success = true;
        }

        if (StringUtils.isBlank(message)) {
            String apiMessage = apiCode.getMessage();
            message = apiMessage;
        }

        return (ApiResult<T>) builder()
                .code(apiCode.getCode())
                .message(message)
                .data(data)
                .success(success)
                .time(new Date())
                .build();
    }

    public static ApiResult<Boolean> ok() {
        return ok(null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return result(ApiCode.SUCCESS, data);
    }

    public static <T> ApiResult<T> ok(T data, String message) {
        return result(ApiCode.SUCCESS, message, data);
    }

    public static ApiResult<Map<String, Object>> okMap(String key, Object value) {
        Map<String, Object> map = new HashMap(1);
        map.put(key, value);
        return ok(map);
    }

    public static ApiResult<Boolean> fail(ApiCode apiCode) {
        return result(apiCode, null);
    }

    public static ApiResult<String> fail(String message) {
        return result(ApiCode.FAIL, message, null);
    }

    public static <T> ApiResult<T> fail(ApiCode apiCode, T data) {
        if (ApiCode.SUCCESS == apiCode) {
            throw new RuntimeException("失败结果状态码不能为" + ApiCode.SUCCESS.getCode());
        } else {
            return result(apiCode, data);
        }
    }

    public static ApiResult<String> fail(Integer errorCode, String message) {
        return (new ApiResult<String>()).setSuccess(false).setCode(errorCode).setMessage(message);
    }

    public static ApiResult<Map<String, Object>> fail(String key, Object value) {
        Map<String, Object> map = new HashMap(1);
        map.put(key, value);
        return result(ApiCode.FAIL, map);
    }

    public static ApiResult<Boolean> fail() {
        return fail(ApiCode.FAIL);
    }

    public static <T> ApiResultBuilder<T> builder() {
        return new ApiResultBuilder();
    }

    public int getCode() {
        return this.code;
    }

    public ApiResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public ApiResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public ApiResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public ApiResult<T> setArgs(Object[] args) {
        this.args = args;
        return this;
    }

    public T getData() {
        return this.data;
    }

    public ApiResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Date getTime() {
        return this.time;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    public ApiResult<T> setTime(Date time) {
        this.time = time;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ApiResult)) {
            return false;
        } else {
            ApiResult<?> other = (ApiResult) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCode() != other.getCode()) {
                return false;
            } else if (this.isSuccess() != other.isSuccess()) {
                return false;
            } else {
                label56:
                {
                    Object this$message = this.getMessage();
                    Object other$message = other.getMessage();
                    if (this$message == null) {
                        if (other$message == null) {
                            break label56;
                        }
                    } else if (this$message.equals(other$message)) {
                        break label56;
                    }

                    return false;
                }

                if (!Arrays.deepEquals(this.getArgs(), other.getArgs())) {
                    return false;
                } else {
                    Object this$data = this.getData();
                    Object other$data = other.getData();
                    if (this$data == null) {
                        if (other$data != null) {
                            return false;
                        }
                    } else if (!this$data.equals(other$data)) {
                        return false;
                    }

                    Object this$time = this.getTime();
                    Object other$time = other.getTime();
                    if (this$time == null) {
                        if (other$time != null) {
                            return false;
                        }
                    } else if (!this$time.equals(other$time)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ApiResult;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getCode();
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getArgs());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $time = this.getTime();
        result = result * 59 + ($time == null ? 43 : $time.hashCode());
        return result;
    }

    public String toString() {
        return "ApiResult(code=" + this.getCode() + ", success=" + this.isSuccess() + ", message=" + this.getMessage() + ", args=" + Arrays.deepToString(this.getArgs()) + ", data=" + this.getData() + ", time=" + this.getTime() + ")";
    }

    public static class ApiResultBuilder<T> {
        private int code;
        private boolean success;
        private String message;
        private Object[] args;
        private T data;
        private Date time;

        ApiResultBuilder() {
        }

        public ApiResultBuilder<T> code(int code) {
            this.code = code;
            return this;
        }

        public ApiResultBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public ApiResultBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResultBuilder<T> args(Object[] args) {
            this.args = args;
            return this;
        }

        public ApiResultBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        @JsonFormat(
                pattern = "yyyy-MM-dd HH:mm:ss"
        )
        public ApiResultBuilder<T> time(Date time) {
            this.time = time;
            return this;
        }

        public ApiResult<T> build() {
            return new ApiResult(this.code, this.success, this.message, this.args, this.data, this.time);
        }

        public String toString() {
            return "ApiResult.ApiResultBuilder(code=" + this.code + ", success=" + this.success + ", message=" + this.message + ", args=" + Arrays.deepToString(this.args) + ", data=" + this.data + ", time=" + this.time + ")";
        }
    }
}
