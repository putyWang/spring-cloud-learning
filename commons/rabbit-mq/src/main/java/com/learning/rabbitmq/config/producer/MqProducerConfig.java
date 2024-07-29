package com.learning.rabbitmq.config.producer;

import com.learning.core.utils.ReflectionUtils;
import com.learning.core.utils.StringUtil;
import com.learning.rabbitmq.aspect.MqSenderAspect;
import com.learning.rabbitmq.config.properties.RabbitProperty;
import com.learning.rabbitmq.converter.MqMessageConverter;
import com.learning.rabbitmq.service.MqSendService;
import com.learning.rabbitmq.service.RabbitMqService;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

import static com.learning.rabbitmq.config.properties.RabbitProperty.*;

/**
 * @ClassName: MqProducerConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@EnableAsync
@Component
@ConditionalOnBean(RabbitProperties.class)
public class MqProducerConfig {

    @Bean(
            name = {"rabbitMqTaskExecutor"}
    )
    @ConditionalOnProperty(
            havingValue = "learning.cloud.rabbit.producer.enable",
            value = "true"
    )
    public AsyncTaskExecutor taskExecutor(RabbitProperty rabbitProperty) {
        ThreadPoolProperty pool = rabbitProperty.getProducer().getPool();
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(pool.getPrefix());
        threadPoolTaskExecutor.setCorePoolSize(pool.getCore());
        threadPoolTaskExecutor.setMaxPoolSize(pool.getMax());
        threadPoolTaskExecutor.setQueueCapacity(pool.getQueueCapacity());
        threadPoolTaskExecutor.setKeepAliveSeconds(pool.getKeepLiva());
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(pool.getAllowTimeOut());
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(pool.getCompleteOnShutdown());
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    @ConditionalOnProperty(
            havingValue = "learning.cloud.rabbit.producer.enable",
            value = "true"
    )
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, RabbitProperty rabbitProperty) {
        ProducerProperty producer = rabbitProperty.getProducer();
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new MqMessageConverter());
        rabbitTemplate.setMandatory(producer.isMandatory());
        rabbitTemplate.setReturnsCallback(
                StringUtil.isEmpty(producer.getReturnCallback()) ?
                        new RabbitMqReturnCallback() : ReflectionUtils.newInstance(producer.getReturnCallback())
        );
        return rabbitTemplate;
    }

    @Bean
    @ConditionalOnBean(RabbitTemplate.class)
    public RabbitMqService rabbitMqService(RabbitTemplate rabbitTemplate) {
        return new RabbitMqService(rabbitTemplate);
    }

    @Bean
    @ConditionalOnBean(RabbitMqService.class)
    public MqSendService mqSendService(RabbitMqService rabbitMqService) {
        return new MqSendService(rabbitMqService);
    }

    @Bean
    @ConditionalOnBean(MqSendService.class)
    public MqSenderAspect mqSenderAspect(MqSendService mqSendService) {
        return new MqSenderAspect(mqSendService);
    }
}

