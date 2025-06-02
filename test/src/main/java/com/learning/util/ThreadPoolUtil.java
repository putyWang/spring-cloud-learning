package com.learning.util;

import cn.hutool.core.util.ObjectUtil;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author WangWei
 * @version v 2.8.1
 * @description 线程池工具类
 * @date 2024-11-01
 **/
public interface ThreadPoolUtil {

    Logger log = LoggerFactory.getLogger(ThreadPoolUtil.class);

    /**
     * 构建基础线程池
     *
     * @return 创建线程池对象
     */
    static ThreadPoolTaskExecutor baseExecutorBuild(Integer maxPoolSize){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        if (corePoolSize == 1) {
            corePoolSize = 4;
        }
        executor.setCorePoolSize(corePoolSize);
        int actualMaxPoolSize = maxPoolSize != null ? maxPoolSize : corePoolSize * 2;
        executor.setMaxPoolSize(actualMaxPoolSize);
        executor.setQueueCapacity(actualMaxPoolSize * 4);;
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(
                (r, executor1) -> {
                    log.error("当前线程池已满，触发拒绝策略, 在使用的线程数为{},最大线程数为{},队列大小为{}", executor1.getActiveCount(), executor1.getMaximumPoolSize(), executor1.getQueue().size());
                }
        );
        executor.setTaskDecorator(
                runnable -> {
                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                    // 将父类请求头保存到子线程中
                    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                    return () -> {
                        try {
                            RequestContextHolder.setRequestAttributes(requestAttributes);
                            // 使用 skywalking 封装
                            RunnableWrapper.of(runnable).run();
                        } finally {
                            RequestContextHolder.resetRequestAttributes();
                        }
                    };
                }
        );
        executor.initialize();
        return executor;
    }

    /**
     * 解析异步结果
     *
     * @param future 异步结果
     * @param msg 消息
     * @param timeOut 超时时间
     * @param <V> 结果类型
     * @return 返回结果
     */
    static <V> V getFutureResult(Future<V> future, String msg, Integer timeOut) {
        V result = null;
        try {
            if(ObjectUtil.isNotNull(timeOut) && ! timeOut.equals(0)) {
                result = future.get(timeOut, TimeUnit.SECONDS);
            } else {
                result = future.get();
            }

        } catch (Exception e) {
            log.error(msg, e);
        }
        return result;
    }
}
