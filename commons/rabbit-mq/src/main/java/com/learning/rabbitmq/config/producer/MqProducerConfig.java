package com.learning.rabbitmq.config.producer;

import com.learning.rabbitmq.converter.MqMessageConverter;
import com.learning.rabbitmq.service.AsyncMqSendService;
import com.learning.rabbitmq.service.MqSendService;
import com.learning.rabbitmq.service.RabbitMqService;
import com.learning.rabbitmq.strategy.RabbitMqReturnCallback;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @ClassName: MqProducerConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@EnableAsync
public class MqProducerConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new MqMessageConverter());
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(new RabbitMqReturnCallback());
        return rabbitTemplate;
    }

    @Bean
    public RabbitMqService rabbitMqService(RabbitTemplate rabbitTemplate) {
        return new RabbitMqService(rabbitTemplate);
    }

    @Bean
    public MqSendService mqSendService(RabbitMqService rabbitMqService) {
        return new MqSendService(rabbitMqService);
    }

    @Bean
    public AsyncMqSendService asyncMqSendService(RabbitMqService rabbitMqService) {
        return new AsyncMqSendService(rabbitMqService);
    }
}

