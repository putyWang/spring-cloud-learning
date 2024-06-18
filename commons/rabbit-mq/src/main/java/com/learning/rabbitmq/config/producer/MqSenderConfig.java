package com.learning.rabbitmq.config.producer;

import org.springframework.context.annotation.Bean;

/**
 * @ClassName: MqSenderConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
public class MqSenderConfig {
    @Bean
    public MqSenderAspect mqSenderAspect() {
        return new MqSenderAspect();
    }
}

