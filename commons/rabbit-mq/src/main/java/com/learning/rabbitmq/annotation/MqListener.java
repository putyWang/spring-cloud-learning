package com.learning.rabbitmq.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: MqListener
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MqListener {
    String exchange();

    String queue();

    String routingKey() default "#";

    boolean enableConsumerAutoExpand() default true;

    int concurrentConsumers() default 1;

    int maxConcurrentConsumers() default 10;

    int startConsumerMinInterval() default 2000;

    /**
     * 是否启用重试
     */
    boolean enableRetry() default false;

    /**
     * 重试时间间隔
     */
    long initialInterval() default 1000L;

    /**
     * 重试时间间隔增长系数
     */
    double multiplier() default 2.0D;

    /**
     * 重试最大时间间隔
     */
    long maxInterval() default 600000L;

    /**
     * 最大重试次数
     */
    int maxAttempts() default 10;
}
