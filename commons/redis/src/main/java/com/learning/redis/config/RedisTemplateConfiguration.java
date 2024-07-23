package com.learning.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

/**
 * @author WangWei
 * @description
 * @date 2024-06-21
 **/
@EnableCaching
@Configuration
public class RedisTemplateConfiguration {

    @Bean(
            name = {"redisTemplate"}
    )
    @ConditionalOnMissingBean({RedisTemplate.class})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<Object> jacksonSerial = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        jacksonSerial.setObjectMapper(
                om.activateDefaultTyping(om.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL)
        );
        StringRedisSerializer stringSerial = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerial);
        redisTemplate.setValueSerializer(jacksonSerial);
        redisTemplate.setHashKeySerializer(stringSerial);
        redisTemplate.setHashValueSerializer(jacksonSerial);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig().entryTtl(Duration.ofHours(1L));
        return RedisCacheManager.builder(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory)
        ).cacheDefaults(redisCacheConfiguration).build();
    }
}

