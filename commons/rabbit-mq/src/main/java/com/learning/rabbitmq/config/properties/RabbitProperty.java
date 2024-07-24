package com.learning.rabbitmq.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description 配置对象
 * @date 2024-07-24
 **/
@Component
@ConfigurationProperties(prefix = "learning.cloud.rabbit")
@Data
public class RabbitProperty {
    /**
     * rabbit 服务地址
     * 多个服务使用 , 号间隔
     */
    private String addresses = "127.0.0.1:5672";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 虚拟主机地址
     */
    private String vhost = "/";

    /**
     * 消费者配置信息
     */
    private List<ConsumerProperty> consumer = new ArrayList<>();

    @Data
    public static class ConsumerProperty {

        /**
         * 队列名
         */
        private String queue;

        /**
         * 交换机名
         */
        private String exchange;

        /**
         * 路由 key
         */
        private String routing;

        /**
         * 消费者自动扩展配置
         */
        private ConsumerAutoExpand consumerAutoExpand;

        /**
         * 消费者重试配置
         */
        private RetryProperties retry;
    }

    @Data
    public static class ConsumerAutoExpand {

        /**
         * 是否启用
         */
        private boolean enable = true;

        /**
         * 消费对象的全限定类名
         */
        private String handler;

        /**
         * 核心消费者线程数
         */
        private Integer concurrentConsumers = 1;

        /**
         * 最大消费者线程数
         */
        private int maxConcurrentConsumers = 10;

        /**
         * 消费者最小启用间隔
         */
        private int startConsumerMinInterval = 2000;
    }

    @Data
    public static class RetryProperties {

        /**
         * 是否启用重试
         */
        private boolean enable = false;

        /**
         * 重试时间间隔
         */
        private long initialInterval = 1000L;

        /**
         * 重试时间间隔增长系数
         */
        private double multiplier = 2.0D;

        /**
         * 重试最大时间间隔
         */
        private long maxInterval = 600000L;

        /**
         * 最大重试次数
         */
        private int maxAttempts = 10;
    }
}
