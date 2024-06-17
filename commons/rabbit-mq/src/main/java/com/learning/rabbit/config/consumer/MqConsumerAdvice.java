package com.learning.rabbit.config.consumer;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;

/**
 * @ClassName: MqConsumerAdvice
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
public class MqConsumerAdvice {
    public static MethodInterceptor methodInterceptor() {
        return RetryInterceptorBuilder.stateless().backOffOptions(1000L, 2.0D, 600000L).maxAttempts(10).build();
    }
}
