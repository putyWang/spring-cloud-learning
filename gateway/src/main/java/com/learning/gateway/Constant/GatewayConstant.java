package com.learning.gateway.Constant;

public interface GatewayConstant {

    /**
     * 常规输出
     */
    String APPLICATION_JSON_REQUEST = "applicationJsonRequest";

    String FORM_DATA_REQUEST = "formDataRequest";

    String BASIC_REQUEST = "basicRequest";

    String NORMAL_REQUEST = "normalRequest";

    /**
     * 慢查询
     */
    String SLOW = "slow";

    /**
     * 非200响应
     */
    String FAIL = "fail";
}
