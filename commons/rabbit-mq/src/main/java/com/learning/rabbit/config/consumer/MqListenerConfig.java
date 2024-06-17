package com.learning.rabbit.config.consumer;

import org.springframework.context.annotation.Bean;

/**
 * @ClassName: MqListenerConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
public class MqListenerConfig {
    @Bean
    public MqListenerAnnotationProcessor mqListenerAnnotationProcessor() {
        return new MqListenerAnnotationProcessor();
    }
}
