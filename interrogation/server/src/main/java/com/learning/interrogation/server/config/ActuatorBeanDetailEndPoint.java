package com.learning.interrogation.server.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/4 下午11:09
 */
@Component
@Endpoint(id = "object-details")
@AllArgsConstructor
public class ActuatorBeanDetailEndPoint {

    private final ApplicationContext applicationContext;

    // 读取操作，通过类名查询对象详情
    @ReadOperation
    public Map<String, Object> getObjectDetails(String beanName) {
        try {
            // 根据类名获取 Class 对象
            Object instance = applicationContext.getBean(beanName);
            if (instance instanceof ThreadPoolTaskExecutor) {
                return getObjectDetails((ThreadPoolTaskExecutor)instance);
            }
            // 存储对象属性信息
            Map<String, Object> properties = new HashMap<>();
            // 获取类的所有字段
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                // 获取字段名称和值
                String fieldName = field.getName();
                Object fieldValue = field.get(instance);
                properties.put(fieldName, fieldValue);
            }
            return properties;
        } catch (Exception e) {
            // 处理异常，返回错误信息
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get object details: " + e.getMessage());
            return error;
        }
    }

    private Map<String, Object> getObjectDetails(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        Map<String, Object> result = new HashMap<>();
        ThreadPoolExecutor threadPoolExecutor = threadPoolTaskExecutor.getThreadPoolExecutor();
        result.put("corePoolSize", threadPoolExecutor.getCorePoolSize());
        result.put("maxPoolSize", threadPoolExecutor.getMaximumPoolSize());
        result.put("currentActive", threadPoolExecutor.getActiveCount());
        result.put("queueSize", threadPoolExecutor.getQueue().size());
        result.put("remainingCapacity", threadPoolExecutor.getQueue().remainingCapacity());
        result.put("totalCapacity", threadPoolExecutor.getQueue().remainingCapacity() + threadPoolExecutor.getQueue().size());
        result.put("poolSize", threadPoolExecutor.getPoolSize());
        return result;
    }
}
