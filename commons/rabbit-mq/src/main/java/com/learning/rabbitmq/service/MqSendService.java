package com.learning.rabbitmq.service;

import com.learning.rabbitmq.domain.BaseMqMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

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

    /**
     * 同步发送消息
     *
     * @param mqMessage mq 消息对象
     */
    public void sendMessage(BaseMqMessage mqMessage) {
        rabbitMqService.sendMessage(mqMessage);
    }

    /**
     * 同步发送消息
     *
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息内容
     */
    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitMqService.sendMessage(exchange, routingKey, message);
    }

    /**
     * 异步发送消息
     *
     * @param mqMessage  mq 消息对象
     */
    @Async("rabbitMqTaskExecutor")
    public void sendMessageAsync(BaseMqMessage mqMessage) {
        this.sendMessage(mqMessage);
    }

    /**
     * 异步发送消息
     *
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息内容
     */
    @Async("rabbitMqTaskExecutor")
    public void sendMessageAsync(String exchange, String routingKey, Object message) {
        this.sendMessage(exchange, routingKey, message);
    }
}
