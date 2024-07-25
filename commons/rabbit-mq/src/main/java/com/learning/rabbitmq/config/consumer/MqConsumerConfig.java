package com.learning.rabbitmq.config.consumer;

import com.learning.core.utils.CollectionUtils;
import com.learning.core.utils.ObjectUtils;
import com.learning.core.utils.ReflectionUtils;
import com.learning.core.utils.StringUtil;
import com.learning.rabbitmq.annotation.MqListener;
import com.learning.rabbitmq.config.properties.RabbitProperty;
import com.learning.rabbitmq.converter.MqMessageConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;

import static com.learning.rabbitmq.config.properties.RabbitProperty.*;

/**
 * @description 消费者配置
 * @author WangWei
 * @date 2024-06-17
 * @version V1.0
 **/
@Configuration
@ConditionalOnProperty(
        value = "learning.cloud.rabbit.consumer.enable",
        havingValue = "true"
)
public class MqConsumerConfig {

    /**
     * 根据配置创建 RabbitAdmin
     * @param connectionFactory 链接工厂
     * @param rabbitProperty 配置属性
     * @return
     */
    @Bean
    @ConditionalOnProperty(
            value = "learning.cloud.rabbit.consumer.enable",
            havingValue = "true"
    )
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory, RabbitProperty rabbitProperty) {
        // 1 创建 rabbitAdmin
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 2 构建 rabbitAdmin
        rabbitAdmin(connectionFactory, rabbitProperty.getConsumer(), rabbitAdmin, null, null);
        return rabbitAdmin;
    }

    /**
     * 构建注解型消费者处理器
     * @param rabbitAdmin rabbitAdmin
     * @param mqConnectionFactory 链接工厂
     * @return 创建的 BeanPostProcessor
     */
    @Bean
    @ConditionalOnBean(RabbitAdmin.class)
    public BeanPostProcessor mqListenerAnnotationProcessor(RabbitAdmin rabbitAdmin, ConnectionFactory mqConnectionFactory) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
                org.springframework.util.ReflectionUtils.doWithMethods(AopUtils.getTargetClass(bean), (method) -> {
                    MqListener mqListener = AnnotationUtils.getAnnotation(method, MqListener.class);

                    if (mqListener != null) {
                        rabbitAdmin(mqConnectionFactory, praseProperty(mqListener), rabbitAdmin, method, bean);
                    }

                });
                return bean;
            }
        };
    }

    private void rabbitAdmin(ConnectionFactory connectionFactory, ConsumerProperty consumer, RabbitAdmin rabbitAdmin, Method method, Object bean) {
        // 1 验证消费者配置列表
        if(consumer == null || CollectionUtils.isEmpty(consumer.getBindingList())) {
            return;
        }
        // 2 根据 consumer 构建 rabbitAdmin
        rabbitAdmin.setAutoStartup(false);
        // 2.1 设置链接工厂
        SimpleMessageListenerContainer messageContainer = new SimpleMessageListenerContainer();
        messageContainer.setConnectionFactory(connectionFactory);
        // 2.2 设置消息处理适配器
        if (ObjectUtils.isNull(method)) {
            setMessageListener(messageContainer, ReflectionUtils.getClass(consumer.getHandler()));
        } else {
            setMessageListener(messageContainer, bean, method);
        }
        // 2.3 设置绑定
        consumer.getBindingList().forEach(binding -> {
            if(StringUtil.isEmpty(binding.getQueueName())) {
                throw new RuntimeException("queues may not be empty");
            } else {
                Queue name = new Queue(binding.getQueueName());
                rabbitAdmin.declareQueue(name);
                messageContainer.addQueues(name);
            }

            if(StringUtil.isEmpty(binding.getExchange())) {
                throw new RuntimeException("exchange may not be empty");
            } else {
                rabbitAdmin.declareBinding(
                        new Binding(binding.getQueueName(), Binding.DestinationType.QUEUE, binding.getExchange(), binding.getRouting(), null)
                );
            }
        });
        // 2.4 设置自动重试参数
        RetryProperties retry = consumer.getRetry();
        if (retry != null && retry.isEnable()) {
            messageContainer.setAdviceChain(
                    RetryInterceptorBuilder.stateless()
                            .backOffOptions(retry.getInitialInterval(), retry.getMultiplier(), retry.getMaxInterval())
                            .maxAttempts(retry.getMaxAttempts()).build()
            );
        }
        // 2.5 设置消息自动扩展参数
        AutoExpand autoExpand = consumer.getAutoExpand();
        if (autoExpand != null && autoExpand.isEnable()) {
            messageContainer.setConcurrentConsumers(autoExpand.getConcurrentConsumers());
            messageContainer.setMaxConcurrentConsumers(autoExpand.getMaxConcurrentConsumers());
            messageContainer.setStartConsumerMinInterval(autoExpand.getStartConsumerMinInterval());
        }
        // 2.6 启动配置
        if (messageContainer.getQueueNames().length > 0 && messageContainer.getMessageListener() != null) {
            messageContainer.start();
        }

    }

    private void setMessageListener(SimpleMessageListenerContainer messageContainer, Class<?> mqMessageHandler) {
        if (messageContainer != null && mqMessageHandler != null) {
            Type typeClass = mqMessageHandler.getGenericSuperclass();
            if (typeClass instanceof ParameterizedType) {
                messageContainer.setMessageListener(new MessageListenerAdapter(mqMessageHandler, new MqMessageConverter()));
            } else {
                messageContainer.setMessageListener(new MessageListenerAdapter(mqMessageHandler, new SimpleMessageConverter()));
            }
        }
    }

    private void setMessageListener(SimpleMessageListenerContainer messageContainer, Object bean, Method method) {
        if (messageContainer != null && bean != null) {
            if (method.getParameters().length != 1) {
                throw new RuntimeException("method parameter's num is only be one");
            }
            MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
            messageListenerAdapter.setDefaultListenerMethod(method.getName());
            messageListenerAdapter.setDelegate(bean);
            messageListenerAdapter.setMessageConverter(new MqMessageConverter());
            messageContainer.setMessageListener(messageListenerAdapter);
        }
    }

    /**
     * 将 MqListener 注解转换为消费者配置
     * @param mqListener 监听注解
     * @return 消费者配置对象
     */
    private ConsumerProperty praseProperty(MqListener mqListener) {
        return new ConsumerProperty()
                // 1 设置 binding 信息
                .setBindingList(Collections.singletonList(
                        new BindingProperty().setQueueName(mqListener.queue())
                                .setExchange(mqListener.exchange())
                                .setRouting(mqListener.routingKey())
                ))
                // 2 设置自动扩展配置
                .setAutoExpand(
                        new AutoExpand().setEnable(mqListener.enableConsumerAutoExpand())
                                .setConcurrentConsumers(mqListener.concurrentConsumers())
                                .setMaxConcurrentConsumers(mqListener.maxConcurrentConsumers())
                                .setStartConsumerMinInterval(mqListener.startConsumerMinInterval())
                )
                // 3 设置重试配置
                .setRetry(
                        new RetryProperties().setEnable(mqListener.enableRetry())
                                .setInitialInterval(mqListener.initialInterval())
                                .setMaxInterval(mqListener.maxInterval())
                                .setMultiplier(mqListener.multiplier())
                                .setMaxAttempts(mqListener.maxAttempts())
                );
    }
}
