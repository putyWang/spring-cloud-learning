package com.learning.rabbit.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @ClassName: EnableMqConsumer
 * @Description: 启用 mq 消费者
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({MqConnectionConfig.class, MqConsumerConfig.class, MqListenerConfig.class})
@Documented
public @interface EnableMqConsumer {
}
