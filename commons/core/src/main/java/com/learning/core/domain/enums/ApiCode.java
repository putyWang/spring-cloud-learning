package com.learning.core.domain.enums;

/**
 * 相应返回类编码
 */
public enum ApiCode {

    NULL(0, "操作成功"),
    SUCCESS(200, "操作成功"),
    UNAUTHORIZED(401, "未登录"),
    NOT_PERMISSION(403, "没有权限"),
    NOT_FOUND(404, "你请求的资源不存在"),
    FAIL(500, "服务器发生错误"),
    ADD_FAIL(1001, "新增操作失败"),
    DELETE_FAIL(1002, "删除操作失败"),
    UPDATE_FAIL(1003, "更新操作失败"),
    QUERY_FAIL(1004, "查询操作失败"),
    FILE_UPLOAD_FAIL(1005, "文件上传失败"),
    FILE_UPLOAD_TYPE(1006, "文件上传类型不支持"),
    FILE_UPLOAD_SIZE(1007, "文件上传超出规定大小"),
    LOGIN_EXCEPTION(4000, "登录失败"),
    LOGIN_NOT(4001, "token已过期或者无效或者登出"),
    USER_NOT_EXIST_ERROR(4002, "用户不存在或用户相关信息有误"),
    OLD_PASSWORD_ERROR(4003, "旧密码错误"),
    USER_NOT_EXIST_ORG_INFO(4004, "用户未设置机构信息"),
    USER_NOT_EXIST_ROLE_INFO(4005, "用户未设置角色信息"),
    MENU_TABLE_CODE_EXIST(4006, "菜单编码已经存在"),
    USER_CODE_IS_EXIST(4007, "用户编码已经存储"),
    AUTH_PASSWORD_NOT_SAME(4008, "密码不正确"),
    TENANT_CODE_EXIST(4009, "租户编码已经存在"),
    TENANT_NAME_EXIST(4010, "租户编码已经存在"),
    ACCOUNT_LOCKED(4011, "账号已被锁定"),
    ERROR_VERIFICATION_CODE(4012, "验证码错误"),
    SYSTEM_EXCEPTION(5000, "系统异常"),
    PARAMETER_EXCEPTION(5001, "请求参数校验异常"),
    PARAMETER_PARSE_EXCEPTION(5002, "请求参数解析异常"),
    HTTP_MEDIA_TYPE_EXCEPTION(5003, "HTTP内容类型异常"),
    CLASS_NOT_FOUND(5004, "类缺失异常"),
    BUSINESS_EXCEPTION(5101, "业务处理异常"),
    DAO_EXCEPTION(5102, "数据库处理异常"),
    VERIFICATION_CODE_EXCEPTION(5103, "验证码校验异常"),
    JWT_DECODE_EXCEPTION(5107, "Token解析异常"),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION(5108, "不支持请求方法"),
    ELASTICSEARCH_CLIENT_EXCEPTION(5109, "Elasticsearch Client 异常"),
    ELASTICSEARCH_REQUEST_EXCEPTION(5110, "Elasticsearch 请求 异常"),
    IP_NOT_IN_WHITELIST(5113, "IP不在白名单中"),
    MSM_EXCEPTION(6000, "短信发送异常"),
    MAIL_EXCEPTION(6001, "邮箱发送异常");

    private final int code;
    private final String message;

    private ApiCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ApiCode getApiCode(int code) {
        ApiCode[] ecs = values();

        for (int i = 0; i < ecs.length; ++i) {
            ApiCode ec = ecs[i];
            if (ec.getCode() == code) {
                return ec;
            }
        }

        return SUCCESS;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
