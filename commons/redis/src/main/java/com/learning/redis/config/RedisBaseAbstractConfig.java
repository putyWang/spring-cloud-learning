package com.learning.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.core.utils.ObjectUtils;
import com.learning.core.utils.StringUtil;
import com.learning.redis.config.properties.RedisProperties;
import com.learning.redis.consts.enums.RedisModel;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import static com.learning.redis.config.properties.RedisProperties.LettucePool;
import static com.learning.redis.config.properties.RedisProperties.Sentinel;

/**
 * @author WangWei
 * @version v 1.0
 * @description 抽象redis配置类
 * @date 2024-06-18
 **/
@Configuration
@Data
@Log4j2
@RequiredArgsConstructor
public abstract class RedisBaseAbstractConfig {

    public static final String REDIS_PREFIX = "redis://";

    /**
     * redis 基础配置
     */
    private final RedisProperties redisProperties;

    /**
     *
     */
    private final ListableBeanFactory beanFactory;

    public RedisTemplate<String, Object> defaultRedisTemplate() {
        // 1 创建 String 序列化对象
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 2 创建 json 序列化对象
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jackson2JsonRedisSerializer.setObjectMapper(
                objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL)
        );
        // 3 构建 redisTemplate
        return redisTemplate(
                lettuceConnectionFactory(this.redisConfiguration(redisProperties.getDatabase())),
                stringRedisSerializer,
                jackson2JsonRedisSerializer,
                stringRedisSerializer,
                jackson2JsonRedisSerializer
        );
    }

    public RedisConfiguration redisConfiguration(Integer database) {
        switch (ObjectUtils.defaultIfNull(redisProperties.getMode(), RedisModel.SINGLE)) {
            case SINGLE:
                RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
                redisStandaloneConfiguration.setHostName(redisProperties.getHost());
                redisStandaloneConfiguration.setPort(Integer.parseInt(redisProperties.getPort()));
                if (null != database) {
                    redisStandaloneConfiguration.setDatabase(database);
                }

                if (StringUtil.isNotBlank(redisProperties.getPassword())) {
                    redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
                }

                return redisStandaloneConfiguration;
            case CLUSTERS:
                RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
                if (StringUtil.isNotBlank(redisProperties.getHost())) {
                    redisClusterConfiguration.setClusterNodes(
                            Arrays.stream(redisProperties.getHost().split(","))
                                    .map(host -> {
                                        String[] hostArray = host.replace("\\s", "")
                                                .replace("\n", "")
                                                .split(":");
                                        return new RedisNode(hostArray[0], Integer.parseInt(hostArray[1]));
                                    }).collect(Collectors.toSet()));
                    redisClusterConfiguration.setMaxRedirects(3);
                    if (StringUtil.isNotBlank(redisProperties.getPassword())) {
                        redisClusterConfiguration.setPassword(redisProperties.getPassword());
                    }
                }
                return redisClusterConfiguration;
            case SENTINEL:
                RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
                Sentinel sentinel = redisProperties.getSentinel();
                if (ObjectUtils.isNotNull(sentinel.getNodes()) && ObjectUtils.isNotNull(sentinel.getMaster())) {
                    configuration.setMaster(sentinel.getMaster());
                    configuration.setSentinels(
                            Arrays.stream(sentinel.getNodes().split(","))
                                    .map(host -> {
                                        String[] hostArray = host.replace("\\s", "")
                                                .replace("\n", "")
                                                .split(":");
                                        return new RedisNode(hostArray[0], Integer.parseInt(hostArray[1]));
                                    }).collect(Collectors.toSet()));
                }

                if (ObjectUtils.isNotNull(database)) {
                    configuration.setDatabase(database);
                }

                if (StringUtil.isNotBlank(redisProperties.getPassword())) {
                    configuration.setPassword(RedisPassword.of(redisProperties.getPassword()));
                }

                return configuration;
            default:
                log.error("不支持当前模式");
                return null;
        }
    }

    @Bean(
            destroyMethod = "shutdown"
    )
    DefaultClientResources clientResources() {
        return DefaultClientResources.create();
    }

    public LettuceConnectionFactory lettuceConnectionFactory(RedisConfiguration redisConfiguration) {
        // 1 构建 lettuce 连接池配置
        GenericObjectPoolConfig<Object> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        LettucePool pool = redisProperties.getLettuce().getPool();
        genericObjectPoolConfig.setEvictorShutdownTimeout(Duration.ofMillis(pool.getShutdownTimeout()));
        genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
        genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
        genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(pool.getMaxWait()));
        // 2 设置 lettuce 链接工厂
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
                redisConfiguration,
                LettucePoolingClientConfiguration.builder()
                        .poolConfig(genericObjectPoolConfig)
                        .clientResources(this.beanFactory.getBean(DefaultClientResources.class))
                        .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                        .build()
        );
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, StringRedisSerializer keySerializer,
                                                       Jackson2JsonRedisSerializer<Object> valueSerializer, StringRedisSerializer hashKeySerializer,
                                                       Jackson2JsonRedisSerializer<Object> hashValueSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(hashKeySerializer);
        redisTemplate.setHashValueSerializer(hashValueSerializer);
        return redisTemplate;
    }
}
