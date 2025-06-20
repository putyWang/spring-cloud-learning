package com.learning.rabbitmq.config.consumer;

import com.learning.rabbitmq.annotation.MqListener;
import com.learning.rabbitmq.converter.MqMessageConverter;
import com.learning.rabbitmq.domain.BindingObject;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * @desc
 * @author WangWei
 * @date 2024-06-17
 * @version V1.0
 **/
public class MqListenerAnnotationProcessor implements BeanPostProcessor {
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private ConnectionFactory mqConnectionFactory;
    private final Boolean retryPolicySet;

    public MqListenerAnnotationProcessor() {
        this.retryPolicySet = Boolean.FALSE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class targetClass = AopUtils.getTargetClass(bean);
        ReflectionUtils.doWithMethods(targetClass, (method) -> {
            MqListener mqListener = AnnotationUtils.getAnnotation(method, MqListener.class);
            if (mqListener != null) {
                this.processRabbitAdmin(mqListener, method, bean);
            }

        });
        return bean;
    }

    protected void processRabbitAdmin(MqListener mqListener, Method method, Object bean) {
        BindingObject bindingObject = new BindingObject();
        bindingObject.addBinding(mqListener.queue(), mqListener.exchange(), mqListener.routingKey());
        SimpleMessageListenerContainer messageContainer = new SimpleMessageListenerContainer();
        messageContainer.setConnectionFactory(this.mqConnectionFactory);
        this.setMessageListener(messageContainer, bean, method);
        this.createExchange(mqListener.exchange());
        Iterator var6 = bindingObject.getQueues().iterator();

        while(var6.hasNext()) {
            Queue queue = (Queue)var6.next();
            this.rabbitAdmin.declareQueue(queue);
            messageContainer.addQueues(new Queue[]{queue});
        }

        var6 = bindingObject.getBindings().iterator();

        while(var6.hasNext()) {
            Binding binding = (Binding)var6.next();
            this.rabbitAdmin.declareBinding(binding);
        }

        if (this.retryPolicySet) {
            messageContainer.setAdviceChain(MqConsumerAdvice.methodInterceptor());
        }

        this.initConsumerConfig(messageContainer, mqListener);
        if (messageContainer.getQueueNames().length > 0 && messageContainer.getMessageListener() != null) {
            messageContainer.start();
        }

    }

    private void createExchange(String exchangeName) {
        TopicExchange topicExchange = new TopicExchange(exchangeName, true, false);
        this.rabbitAdmin.declareExchange(topicExchange);
    }

    protected void setMessageListener(SimpleMessageListenerContainer messageContainer, Object bean, Method method) {
        if (messageContainer != null && bean != null) {
            if (method.getParameters().length != 1) {
                throw new RuntimeException("method parameter's num is only be one");
            } else {
                MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
                messageListenerAdapter.setDefaultListenerMethod(method.getName());
                messageListenerAdapter.setDelegate(bean);
                messageListenerAdapter.setMessageConverter(new MqMessageConverter());
                messageContainer.setMessageListener(messageListenerAdapter);
            }
        }
    }

    protected void initConsumerConfig(SimpleMessageListenerContainer messageContainer, MqListener mqListener) {
        if (mqListener.enableConsumerAutoExpand()) {
            messageContainer.setConcurrentConsumers(mqListener.concurrentConsumers());
            messageContainer.setMaxConcurrentConsumers(mqListener.maxConcurrentConsumers());
            messageContainer.setStartConsumerMinInterval(mqListener.startConsumerMinInterval());
        }

    }
}

