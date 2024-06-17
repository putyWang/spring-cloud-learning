package com.learning.rabbit.config.producer;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName: AsyncTaskExecutorConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
public class AsyncTaskExecutorConfig {
    private static final String THREAD_POOL_NAME_PREFIX = "rabbitmq-taskExecutor-";
    private static final Integer CORE_POOL_SIZE = 1;
    private static final Integer MAX_POOL_SIZE = 10;
    private static final Integer QUEUE_CAPACITY = 1000;
    private static final Integer KEEP_ALIVE_SECONDS = 300;
    private static final Boolean ALLOW_CORE_THREAD_TIME_OUT;
    private static final Boolean WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN;

    @Bean(
            name = {"yhCloudRibbotmqTaskExecutor"}
    )
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("rabbitmq-taskExecutor-");
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

    static {
        ALLOW_CORE_THREAD_TIME_OUT = Boolean.FALSE;
        WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = Boolean.FALSE;
    }
}

