package com.learning.rabbitmq.config.producer;

import com.learning.rabbitmq.converter.MqMessageConverter;
import com.learning.rabbitmq.service.MqSendService;
import com.learning.rabbitmq.service.RabbitMqService;
import com.learning.rabbitmq.strategy.RabbitMqReturnCallback;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * @ClassName: MqProducerConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@EnableAsync
@Component
@ConditionalOnClass(RabbitProperties.class)
public class MqProducerConfig {

    @Bean
    @ConditionalOnBean(ConnectionFactory.class)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new MqMessageConverter());
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(new RabbitMqReturnCallback());
        return rabbitTemplate;
    }

    @Bean
    @ConditionalOnBean(RabbitTemplate.class)
    public RabbitMqService rabbitMqService(RabbitTemplate rabbitTemplate) {
        return new RabbitMqService(rabbitTemplate);
    }

    @Bean
    @ConditionalOnBean(RabbitMqService.class)
    public MqSendService mqSendService(RabbitMqService rabbitMqService) {
        return new MqSendService(rabbitMqService);
    }

    @Bean
    @ConditionalOnBean(MqSendService.class)
    public MqSenderAspect mqSenderAspect(MqSendService mqSendService) {
        return new MqSenderAspect(mqSendService);
    }
}

