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
}
