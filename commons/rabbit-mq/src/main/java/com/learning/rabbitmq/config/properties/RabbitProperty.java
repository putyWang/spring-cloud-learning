package com.learning.rabbitmq.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description 配置对象
 * @date 2024-07-24
 **/
@Component
@ConfigurationProperties(prefix = "learning.cloud.rabbit")
@ConditionalOnProperty(
        value = "learning.cloud.rabbit.enable",
        havingValue = "true"
)
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
    private String username = "";

    /**
     * 密码
     */
    private String password = "";

    /**
     * 虚拟主机地址
     */
    private String vhost = "/";

    /**
     * 是否打印执行回执
     */
    private Boolean publisherReturns = true;

    /**
     * 消费者配置信息
     */
    private ConsumerProperty consumer;

    /**
     * 生产者者配置信息
     */
    private ProducerProperty producer;

    @Data
    @Accessors(chain = true)
    public static class ProducerProperty {

        /**
         * 是否启用
         */
        private boolean enable = true;

        /**
         * 是否启用发送失败监听
         */
        private boolean mandatory = true;

        /**
         * 发送失败响应
         */
        private String returnCallback;

        /**
         * 连接池配置
         */
        private ThreadPoolProperty pool = new ThreadPoolProperty();
    }

    @Data
    @Accessors(chain = true)
    public static class ConsumerProperty {

        /**
         * 监听队列数组控制
         */
        private List<BindingProperty> bindingList;

        /**
         * 消费对象的全限定类名
         */
        private String handler;

        /**
         * 消费者自动扩展配置
         */
        private AutoExpand autoExpand = new AutoExpand();

        /**
         * 消费者重试配置
         */
        private RetryProperties retry = new RetryProperties();
    }

    @Data
    @Accessors(chain = true)
    public static class BindingProperty {
        /**
         * 队列名
         */
        private QueueProperty queue;

        /**
         * 交换机名
         */
        private ExchangeProperty exchange;

        /**
         * 路由 key
         */
        private String routing = "#";
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QueueProperty {
        /**
         * 名称
         */
        private String name;

        /**
         * 是否持久化
         */
        private boolean durable = true;

        /**
         * 是否自动删除
         */
        private boolean autoDelete = false;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExchangeProperty{

        /**
         * 主题类型
         * TOPIC
         * FANOUT
         * DIRECT
         */
        private String type = "TOPIC";

        /**
         * 名称
         */
        private String name;

        /**
         * 是否持久化
         */
        private boolean durable = false;

        /**
         * 是否自动删除
         */
        private boolean autoDelete = false;
    }

    @Data
    @Accessors(chain = true)
    public static class AutoExpand {

        /**
         * 是否启用
         */
        private boolean enable = true;

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
    @Accessors(chain = true)
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

    /**
     * 发送方连接池配置
     */
    @Data
    public static class ThreadPoolProperty {

        /**
         * 线程池前缀
         */
        private String prefix = "rabbitmq-taskExecutor-";

        /**
         * 核心线程数
         */
        private int core = 1;

        /**
         * 最大线程数
         */
        private int max = 10;

        /**
         * 队列容量
         */
        private int queueCapacity = 1000;

        /**
         * 活跃时间
         */
        private int keepLiva = 10;

        /**
         * 是否允许核心线程超时
         */
        private Boolean allowTimeOut = false;

        /**
         * 是否等待所有任务结束后再结束
         */
        private Boolean completeOnShutdown = false;
    }
}
