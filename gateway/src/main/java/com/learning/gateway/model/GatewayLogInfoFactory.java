package com.learning.gateway.model;

import com.learning.core.utils.StringUtils;
import com.learning.gateway.Constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GatewayLogInfoFactory {
    public static void log(String type, GatewayLog gatewayLog){
        switch (type){
            case GatewayConstant.APPLICATION_JSON_REQUEST:
            case GatewayConstant.FORM_DATA_REQUEST:
            case GatewayConstant.BASIC_REQUEST:
                log.info("[{}] {} {},route: {},status: {},excute: {} mills,requestBody: {}"
                        ,gatewayLog.getIp()
                        ,gatewayLog.getMethod()
                        ,gatewayLog.getRequestPath()
                        ,gatewayLog.getTargetServer()
                        ,gatewayLog.getCode()
                        ,gatewayLog.getExecuteTime()
                        ,StringUtils.replace(gatewayLog.getRequestBody(), "\n","")
                );
                break;
            case GatewayConstant.NORMAL_REQUEST:
                log.info("[{}] {} {},route: {},status: {},excute: {} mills,queryParams: {}"
                        ,gatewayLog.getIp()
                        ,gatewayLog.getMethod()
                        ,gatewayLog.getRequestPath()
                        ,gatewayLog.getTargetServer()
                        ,gatewayLog.getCode()
                        ,gatewayLog.getExecuteTime()
                        ,gatewayLog.getQueryParams()
                );
                break;
            default:
                break;
        }
    }
}
