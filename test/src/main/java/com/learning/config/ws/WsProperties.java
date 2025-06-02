package com.learning.config.ws;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/24 下午8:54
 */
@ConfigurationProperties(prefix = "ws")
@ConditionalOnProperty(value = "ws.enable", havingValue = "true")
@Component
@Data
public class WsProperties {

    /**
     * 端点标识
     */
    private String endpoint = "/ws";

    /**
     * 允许跨域路径
     */
    private String allowedOriginPatterns = "*";

    /**
     * 链接超时时间
     * 默认 45 秒
     */
    private Long timeOut = 45L;

    /**
     * 心跳检测时间
     * 默认 15 秒
     */
    private Long heartBeatTime = 15L;

//    /**
//     * broker 订阅前缀
//     */
//    private String broker = "/topic";
//
//    /**
//     * 消息前缀
//     */
//    private String msgPrefix = "/app";
}
