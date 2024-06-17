package com.learning.rabbit.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @ClassName: EnableMqProducer
 * @Description: 启用 mq 生产者
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({MqConnectionConfig.class, AsyncTaskExecutorConfig.class, MqProducerConfig.class, MqSenderConfig.class})
@Documented
public @interface EnableMqProducer {
}
