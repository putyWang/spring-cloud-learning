package com.learning.rabbitmq.handler;

/**
 * @description mq 消息处理器
 * @author WangWei
 * @date 2024-06-17
 * @version V1.0
 **/
public interface MqMessageHandler<T> {

    /**
     * 处理消息
     * @param message
     */
    void handleMessage(T message);
}
