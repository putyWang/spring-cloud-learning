package com.learning.rabbitmq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.core.utils.StringUtil;
import com.learning.rabbitmq.domain.BaseMqMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: RabbitMqService
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Log4j2
@AllArgsConstructor
public class RabbitMqService {
    /**
     *
     */
    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(BaseMqMessage baseMqMessage) {
        if (baseMqMessage == null) {
            throw new RuntimeException("invalid mqMessage is null");
        }

        sendMessage(this.getExchange(baseMqMessage), this.getRoutingKey(baseMqMessage), baseMqMessage);
    }

    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.setReceiveTimeout(1L);

        if (message == null) {
            throw new RuntimeException("invalid mqMessage is null");
        }

        if (StringUtil.isEmpty(exchange)) {
            throw new RuntimeException("invalid exchange is empty");
        }

        if (StringUtil.isEmpty(routingKey)) {
            throw new RuntimeException("invalid routingKey is empty");
        }

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info(String.format("send a mq message exchange[%s] routingKey[%s] content[%s],", exchange, routingKey, OBJECTMAPPER.writeValueAsString(message)));
        } catch (AmqpException | JsonProcessingException exception) {
            log.error(exception);
        }
    }

    private String getRoutingKey(BaseMqMessage baseMqMessage) {
        if (baseMqMessage == null) {
            throw new RuntimeException("invalid mqMessage, mqMessage is null");
        }

        String routingKey = baseMqMessage.getRoutingKey();

        if (StringUtil.isEmpty(routingKey)) {
            throw new RuntimeException("invalid routingKey, routingKey is empty");
        }

        return routingKey;
    }

    private String getExchange(BaseMqMessage baseMqMessage) {
        if (baseMqMessage == null) {
            throw new RuntimeException("invalid mqMessage, mqMessage is null");
        }

        String exchange = baseMqMessage.getExchange();

        if (StringUtil.isEmpty(exchange)) {
            throw new RuntimeException("invalid exchange, exchange is empty");
        }

        return exchange;
    }
}

