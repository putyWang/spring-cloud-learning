package com.learning.skywalking.plugin.threadpool;

import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.meter.Gauge;
import org.apache.skywalking.apm.agent.core.meter.MeterFactory;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

public class ThreadPoolTaskExecutorInterceptor implements InstanceMethodsAroundInterceptor {

    /**
     * 用于存储每个线程池的指标数据
     */
    private static final Map<ThreadPoolTaskExecutor, ThreadPoolMetrics> metricsMap = new HashMap<>();
    private static final ILog log = LogManager.getLogger(ThreadPoolTaskExecutorInterceptor.class);

    static class ThreadPoolMetrics {
        private volatile int activeCount = 0;
        private volatile long completedTaskCount = 0;
        private volatile int queueSize = 0;
        private volatile int poolSize = 0;
        private volatile String threadName = "";

        private Gauge activeCountGauge;
        private Gauge completedTaskCountGauge;
        private Gauge queueSizeGauge;
        private Gauge poolSizeGauge;

        ThreadPoolMetrics(String threadPoolName) {
            Supplier<Double> activeCountSupplier = () -> (double) activeCount;
            Supplier<Double> completedTaskCountSupplier = () -> (double) completedTaskCount;
            Supplier<Double> queueSizeSupplier = () -> (double) queueSize;
            Supplier<Double> poolSizeSupplier = () -> (double) poolSize;

            activeCountGauge = MeterFactory.gauge(threadPoolName + "_active_count", activeCountSupplier).build();
            completedTaskCountGauge = MeterFactory.gauge(threadPoolName + "_completed_task_count", completedTaskCountSupplier).build();
            queueSizeGauge = MeterFactory.gauge(threadPoolName + "_queue_size", queueSizeSupplier).build();
            poolSizeGauge = MeterFactory.gauge(threadPoolName + "_pool_size", poolSizeSupplier).build();
        }
    }

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        if (objInst instanceof ThreadPoolTaskExecutor) {
            ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) objInst;
            ThreadPoolMetrics metrics = metricsMap.computeIfAbsent(executor, k -> new ThreadPoolMetrics(executor.getThreadNamePrefix()));

            ThreadPoolExecutor nativeExecutor = executor.getThreadPoolExecutor();
            if (nativeExecutor != null) {
                metrics.activeCount = nativeExecutor.getActiveCount();
                metrics.completedTaskCount = nativeExecutor.getCompletedTaskCount();
                metrics.queueSize = nativeExecutor.getQueue().size();
                metrics.poolSize = nativeExecutor.getPoolSize();
                metrics.threadName = Thread.currentThread().getName();
            }
        }
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {
        log.error("Exception occurred in ThreadPoolTaskExecutor method", t);
    }
}
