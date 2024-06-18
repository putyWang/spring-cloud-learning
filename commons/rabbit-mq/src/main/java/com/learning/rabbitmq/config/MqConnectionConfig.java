package com.learning.rabbitmq.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @ClassName: MqConnectionConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
public class MqConnectionConfig {
    @Autowired
    private Environment environment;

    @Bean
    public ConnectionFactory mqConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(this.environment.getRequiredProperty("rabbitmq.addresses"));
        connectionFactory.setUsername(this.environment.getRequiredProperty("rabbitmq.username"));
        connectionFactory.setPassword(this.environment.getRequiredProperty("rabbitmq.password"));
        connectionFactory.setVirtualHost(this.environment.getRequiredProperty("rabbitmq.vhost"));
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }
}
