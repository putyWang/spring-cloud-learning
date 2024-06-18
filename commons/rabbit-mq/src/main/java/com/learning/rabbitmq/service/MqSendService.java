package com.learning.rabbitmq.service;

import com.learning.rabbitmq.domain.BaseMqMessage;
import lombok.RequiredArgsConstructor;

/**
 * @ClassName: MqSendService
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@RequiredArgsConstructor
public class MqSendService {

    private final RabbitMqService rabbitMqService;

    public void sendMessage(BaseMqMessage mqMessage) {
        this.rabbitMqService.sendMessage(mqMessage);
    }

    public void sendMessage(String exchange, String routingKey, Object message) {
        this.rabbitMqService.sendMessage(exchange, routingKey, message);
    }
}
