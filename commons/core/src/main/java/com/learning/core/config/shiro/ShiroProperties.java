package com.learning.core.config.shiro;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.shiro")
public class ShiroProperties {

    /**
     * 权限拦截忽略的路径
     */
    private String authIgnore = "login";

    /**
     * redis 链接配置
     */
    private RedisProperty redis = new RedisProperty();

    /**
     * session 链接配置
     */
    private SessionProperty session = new SessionProperty();

    /**
     * Redis 配置
     */
    @Data
    public static class RedisProperty {

        /**
         * 地址默认本地
         */
        private String host = "127.0.0.1";

        /**
         * 端口默认为 6379
         */
        private int port = 6379;

        /**
         * 过期时间默认为不过期
         */
        private int expire;

        /**
         * 超时时间
         */
        private int timeout;

        /**
         * 密码
         */
        private String password;
    }

    /**
     * session 配置
     */
    @Data
    public static class SessionProperty {

        /**
         * session 超时，以毫秒为单位，默认 30 分钟
         */
        private long timeout = 1000 * 60 * 30;

        /**
         * session key 名
         */
        private String sessionName = "shiro_session";
    }
}
