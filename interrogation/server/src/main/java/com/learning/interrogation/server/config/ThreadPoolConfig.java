package com.learning.interrogation.server.config;

import com.learning.interrogation.server.util.ThreadPoolUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author lihaoru
 * @Description 线程池管理
 * @ClassName ThreadPoolConfig
 * @date 2021/06/18 11:31
 **/
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "interrogationExecutor")
    @Primary
    public ThreadPoolTaskExecutor interrogationExecutor() {
        ThreadPoolTaskExecutor executor = ThreadPoolUtil.baseExecutorBuild(500);
        executor.setThreadNamePrefix("interrogationExecutor-Thread");
        return executor;
    }
}
