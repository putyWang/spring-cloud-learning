package com.learning.redis.config;

import com.learning.redis.config.properties.RedisProperties;
import com.learning.redis.utils.RedisUtil;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

/**
 * @author WangWei
 * @version v 1.0
 * @description 基础配置
 * @date 2024-06-18
 **/
@EqualsAndHashCode(callSuper = true)
@Configuration("defaultRedisConfig")
@Log4j2
@ConditionalOnProperty(
        name = "learning.cloud.redis.enable",
        havingValue = "true"
)
public class DefaultRedisConfig extends RedisBaseAbstractConfig {

    public DefaultRedisConfig(RedisProperties redisProperties, ListableBeanFactory beanFactory) {
        super(redisProperties, beanFactory);
    }

    @Bean({"learningRedisTemplate"})
    public RedisTemplate<String, Object> redisTemplate() {
        return super.defaultRedisTemplate();
    }

    @Bean({"learningRedisUtil"})
    @Order(-2147483646)
    public RedisUtil getLearningRedisUtil(@Qualifier("learningRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        return new RedisUtil(redisTemplate, getRedisProperties().getTimeout());
    }

    @PostConstruct
    public void init() {
        log.info("learning-redis-{} init ..", getClass().getPackage().getImplementationVersion());
    }
}
