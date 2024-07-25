package com.learning.rabbitmq.config;

import com.learning.rabbitmq.config.properties.RabbitProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName: MqConnectionConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@ConditionalOnBean(RabbitProperty.class)
@RequiredArgsConstructor
public class MqConnectionConfig {

    private final RabbitProperty property;

    @Bean
    public ConnectionFactory mqConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(property.getAddresses());
        connectionFactory.setUsername(property.getUsername());
        connectionFactory.setPassword(property.getPassword());
        connectionFactory.setVirtualHost(property.getVhost());
        connectionFactory.setPublisherReturns(property.getPublisherReturns());
        return connectionFactory;
    }
}
