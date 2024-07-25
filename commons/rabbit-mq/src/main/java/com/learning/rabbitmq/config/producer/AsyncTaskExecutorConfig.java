package com.learning.rabbitmq.config.producer;

import com.learning.rabbitmq.config.properties.RabbitProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description 异步发送消息线程池配置
 * @author  WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@ConditionalOnBean(RabbitProperty.class)
@Component
public class AsyncTaskExecutorConfig {
    /**
     * 线程池前缀
     */
    private static final String THREAD_POOL_NAME_PREFIX = "rabbitmq-taskExecutor-";

    /**
     * 核心线程数
     */
    private static final Integer CORE_POOL_SIZE = 1;

    /**
     * 最大线程数
     */
    private static final Integer MAX_POOL_SIZE = 10;

    /**
     * 队列容量
     */
    private static final Integer QUEUE_CAPACITY = 1000;

    /**
     * 活跃时间
     */
    private static final Integer KEEP_ALIVE_SECONDS = 300;

    /**
     * 是否允许核心线程超时
     */
    private static final Boolean ALLOW_CORE_THREAD_TIME_OUT = Boolean.FALSE;

    /**
     * 是否等待所有任务结束后再结束
     */
    private static final Boolean WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = Boolean.FALSE;

    @Bean(
            name = {"rabbitMqTaskExecutor"}
    )
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_POOL_NAME_PREFIX);
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        threadPoolTaskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(ALLOW_CORE_THREAD_TIME_OUT);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}

