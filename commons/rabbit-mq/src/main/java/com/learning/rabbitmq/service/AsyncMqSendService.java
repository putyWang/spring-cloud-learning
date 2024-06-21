package com.learning.rabbitmq.service;

import com.learning.rabbitmq.domain.BaseMqMessage;
import org.springframework.scheduling.annotation.Async;

/**
 * @ClassName: AsyncMqSendService
 * @Description: 异步消息发送服务
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
public class AsyncMqSendService extends MqSendService{

    public AsyncMqSendService(RabbitMqService rabbitMqService) {
        super(rabbitMqService);
    }

    /**
     * 异步发送消息
     *
     * @param mqMessage
     */
    @Async("taskExecutor")
    @Override
    public void sendMessage(BaseMqMessage mqMessage) {
        super.sendMessage(mqMessage);
    }

    /**
     * 异步发送消息
     *
     * @param exchange
     * @param routingKey
     * @param message
     */
    @Async("taskExecutor")
    @Override
    public void sendMessage(String exchange, String routingKey, Object message) {
        super.sendMessage(exchange, routingKey, message);
    }
}