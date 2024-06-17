package com.learning.rabbit.domain;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * @ClassName: MqMessageHandler
 * @Description: mq 消息处理器
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
public abstract class MqMessageHandler<T> {
    /**
     *
     */
    protected Boolean enableConsumerAutoExpand;

    /**
     *
     */
    protected Integer concurrentConsumers;

    /**
     *
     */
    protected Integer maxConcurrentConsumers;

    /**
     *
     */
    protected Integer startConsumerMinInterval;

    public MqMessageHandler() {
        this.enableConsumerAutoExpand = Boolean.TRUE;
        this.concurrentConsumers = 1;
        this.maxConcurrentConsumers = 10;
        this.startConsumerMinInterval = 2000;
    }

    /**
     * 对 BindingObject 对象进行初始化
     * @return 初始化后的绑定对象
     */
    public abstract BindingObject initBinding();

    /**
     * 处理消息
     * @param message
     */
    public abstract void handleMessage(T message);

    public void adjustContainer(SimpleMessageListenerContainer messageContainer) {
        if (this.enableConsumerAutoExpand) {
            messageContainer.setConcurrentConsumers(this.concurrentConsumers);
            messageContainer.setMaxConcurrentConsumers(this.maxConcurrentConsumers);
            messageContainer.setStartConsumerMinInterval((long)this.startConsumerMinInterval);
        }

    }

    public boolean isAutoBinding() {
        return true;
    }
}
