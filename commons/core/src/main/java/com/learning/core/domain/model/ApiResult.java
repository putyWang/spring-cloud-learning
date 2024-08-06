package com.learning.core.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.core.domain.enums.ApiCode;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public static <T> ApiResult<T> fail(ApiCode apiCode) {
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

    public static <T> ApiResult<T> fail(Integer errorCode, String message) {
        return (new ApiResult<T>()).setSuccess(false).setCode(errorCode).setMessage(message);
    }

    public static ApiResult<Map<String, Object>> fail(String key, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(key, value);
        return result(ApiCode.FAIL, map);
    }

    public static <T> ApiResult<T> fail() {
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

    @Data
    @NoArgsConstructor
    public static class ApiResultBuilder<T> {
        private int code;
        private boolean success;
        private String message;
        private Object[] args;
        private T data;
        private Date time;

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
