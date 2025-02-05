package com.learning.temp.config;

import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 */
@Configuration
public class PoolConfig {

    @Bean("commonThreadPoolExecutor")
    public ThreadPoolTaskExecutor commonThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor commonThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        commonThreadPoolTaskExecutor.setCorePoolSize(4);
        commonThreadPoolTaskExecutor.setMaxPoolSize(50);
        commonThreadPoolTaskExecutor.setQueueCapacity(1000);
        commonThreadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        commonThreadPoolTaskExecutor.setThreadNamePrefix("common-thread-pool-task");

        // 包装 Runnable 任务以支持 SkyWalking 追踪
        commonThreadPoolTaskExecutor.setTaskDecorator(task -> {
            if (task instanceof Runnable) {
                return RunnableWrapper.of((Runnable) task);
            }
            return task;
        });
        return commonThreadPoolTaskExecutor;
    }
}
