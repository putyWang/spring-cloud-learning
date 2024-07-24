package com.learning.rabbitmq.config.consumer;

import com.learning.rabbitmq.converter.MqMessageConverter;
import com.learning.rabbitmq.domain.BindingObject;
import com.learning.rabbitmq.domain.MqMessageHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @ClassName: MqConsumerConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
public class MqConsumerConfig {

    /**
     * 链式 mq 消息处理器
     */
    @Autowired(
            required = false
    )
    private List<MqMessageHandler<?>> mqMessageHandlers;
    private final Boolean retryPolicySet;

    public MqConsumerConfig() {
        this.retryPolicySet = Boolean.TRUE;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        // 1 创建 rabbitAdmin 并关闭自动启动
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(false);
        // 2 根据 mqMessageHandler 集合构建 rabbitAdmin
        if (CollectionUtils.isNotEmpty(this.mqMessageHandlers)) {
            for (MqMessageHandler<?> mqMessageHandler : mqMessageHandlers) {
                // 2.1 只设置配置了自动绑定的处理器
                if (!mqMessageHandler.isAutoBinding()) {
                    continue;
                }
                //
                BindingObject bindingObject = mqMessageHandler.initBinding();
                SimpleMessageListenerContainer messageContainer = new SimpleMessageListenerContainer();
                messageContainer.setConnectionFactory(connectionFactory);
                if (CollectionUtils.isEmpty(bindingObject.getQueues())) {
                    throw new RuntimeException("queues may not be empty");
                }

                if (CollectionUtils.isEmpty(bindingObject.getBindings())) {
                    throw new RuntimeException("bindings may not be empty");
                }

                setMessageListener(messageContainer, mqMessageHandler);
                bindingObject.getQueues().forEach(queue -> {
                    rabbitAdmin.declareQueue(queue);
                    messageContainer.addQueues(queue);
                });

                bindingObject.getBindings().forEach(rabbitAdmin::declareBinding);

                if (this.retryPolicySet) {
                    messageContainer.setAdviceChain(MqConsumerAdvice.methodInterceptor());
                }

                mqMessageHandler.adjustContainer(messageContainer);
                if (messageContainer.getQueueNames().length > 0 && messageContainer.getMessageListener() != null) {
                    messageContainer.start();
                }
            }
        }
        return rabbitAdmin;
    }

    protected void setMessageListener(SimpleMessageListenerContainer messageContainer, MqMessageHandler<?> mqMessageHandler) {
        if (messageContainer != null && mqMessageHandler != null) {
            Type typeClass = mqMessageHandler.getClass().getGenericSuperclass();
            if (typeClass instanceof ParameterizedType) {
                messageContainer.setMessageListener(new MessageListenerAdapter(mqMessageHandler, new MqMessageConverter()));
            } else {
                messageContainer.setMessageListener(new MessageListenerAdapter(mqMessageHandler, new SimpleMessageConverter()));
            }
        }
    }
}
