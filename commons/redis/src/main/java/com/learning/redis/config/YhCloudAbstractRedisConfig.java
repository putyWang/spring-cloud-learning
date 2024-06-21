package com.learning.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.core.utils.StringUtil;
import com.learning.redis.consts.enums.RedisModel;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static com.fasterxml.jackson.databind.ObjectMapper.*;
/**
 * @author WangWei
 * @version v 1.0
 * @description 抽象redis配置类
 * @date 2024-06-18
 **/
@Configuration
@Data
@Log4j2
public abstract class YhCloudAbstractRedisConfig {

    public static final String REDIS_PREFIX = "redis://";
    @Value("${yanhua.cloud.redis.mode:SINGLE}")
    public RedisModel mode;
    @Value("${yanhua.cloud.redis.host:#{null}}")
    public String host = null;
    @Value("${yanhua.cloud.redis.port:#{null}}")
    public String port = null;
    @Value("${yanhua.cloud.redis.timeout:3000}")
    public int timeout;
    @Value("${yanhua.cloud.redis.sentinel.nodes:#{null}}")
    public String nodes = null;
    @Value("${yanhua.cloud.redis.sentinel.master:#{null}}")
    public String master = null;
    @Value("${yanhua.cloud.redis.password:#{null}}")
    public String password = null;
    @Value("${yanhua.cloud.redis.lettuce.shutdown-timeout:100}")
    public int shutdownTimeout = 100;
    @Value("${yanhua.cloud.redis.lettuce.masterMinSize:10}")
    public int masterMinSize = 10;
    @Value("${yanhua.cloud.redis.lettuce.slaveMinSize:10}")
    public int slaveMinSize = 10;
    @Value("${yanhua.cloud.redis.lettuce.pool.max-active:200}")
    public int maxActive = 200;
    @Value("${yanhua.cloud.redis.lettuce.pool.max-idle:10}")
    public int maxIdle = 10;
    @Value("${yanhua.cloud.redis.lettuce.pool.max-wait:10000}")
    public int maxWait = 10000;
    @Value("${yanhua.cloud.redis.lettuce.pool.min-idle:5}")
    public int minIdle = 5;
    @Autowired
    ListableBeanFactory beanFactory;

    public RedisConfiguration redisConfiguration(Integer database) {
        if (this.mode.equals(RedisModel.SINGLE)) {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(this.host);
            redisStandaloneConfiguration.setPort(Integer.parseInt(this.port));
            if (null != database) {
                redisStandaloneConfiguration.setDatabase(database);
            }

            if (null != this.password && !"".equals(this.password)) {
                redisStandaloneConfiguration.setPassword(this.password);
            }

            return redisStandaloneConfiguration;
        } else {
            String[] hostses;
            int var6;
            String port;
            if (this.mode.equals(RedisModel.CLUSTERS)) {
                RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
                Set<RedisNode> nodes = new HashSet<>();
                hostses = this.host.split(",");
                String[] var15 = hostses;
                var6 = hostses.length;

                for (int var16 = 0; var16 < var6; ++var16) {
                    String h = var15[var16];
                    h = h.replaceAll("\\s", "").replaceAll("\n", "");
                    if (!"".equals(this.host)) {
                        String[] split = h.split(":");
                        nodes.add(new RedisNode(split[0], Integer.parseInt(split[1])));
                    }
                }

                redisClusterConfiguration.setClusterNodes(nodes);
                redisClusterConfiguration.setMaxRedirects(3);
                if (null != this.password && !"".equals(this.password)) {
                    redisClusterConfiguration.setPassword(this.password);
                }

                return redisClusterConfiguration;
            } else if (!this.mode.equals(RedisModel.SENTINEL)) {
                System.err.println("redis 模式配置错误");
                return null;
            } else {
                RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
                if (null != this.nodes && null != this.master) {
                    String[] host = this.nodes.split(",");
                    hostses = host;
                    int var5 = host.length;

                    for (var6 = 0; var6 < var5; ++var6) {
                        String redisHost = hostses[var6];
                        String[] item = redisHost.split(":");
                        String ip = item[0];
                        port = item[1];
                        configuration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
                    }

                    configuration.setMaster(this.master);
                }

                if (null != database) {
                    configuration.setDatabase(database);
                }

                if (!StringUtil.isEmpty(this.password)) {
                    configuration.setPassword(RedisPassword.of(this.password));
                }

                return configuration;
            }
        }
    }

    @Bean(
            destroyMethod = "shutdown"
    )
    DefaultClientResources clientResources() {
        return DefaultClientResources.create();
    }

    public LettuceConnectionFactory lettuceConnectionFactory(RedisConfiguration redisConfiguration) {
        GenericObjectPoolConfig<Object> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxIdle(this.maxIdle);
        genericObjectPoolConfig.setMinIdle(this.minIdle);
        genericObjectPoolConfig.setMaxTotal(this.maxActive);
        genericObjectPoolConfig.setMaxWaitMillis(this.maxWait);
        genericObjectPoolConfig.setEvictorShutdownTimeoutMillis(this.shutdownTimeout);
        DefaultClientResources clientResources = this.beanFactory.getBean(DefaultClientResources.class);
        LettucePoolingClientConfiguration lettucePoolingClientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(genericObjectPoolConfig).clientResources(clientResources).commandTimeout(Duration.ofMillis((long) this.timeout)).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration, lettucePoolingClientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    public RedisTemplate<String, Object> yhCloudDefaultRedisTemplate(Integer databse) {
        RedisConfiguration redisConfiguration = this.redisConfiguration(databse);
        LettuceConnectionFactory lettuceConnectionFactory = this.lettuceConnectionFactory(redisConfiguration);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return this.redisTemplate(lettuceConnectionFactory, stringRedisSerializer, jackson2JsonRedisSerializer, stringRedisSerializer, jackson2JsonRedisSerializer);
    }

    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer keySerializer, RedisSerializer valueSerializer, RedisSerializer hashKeySerializer, RedisSerializer hashValueSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(hashKeySerializer);
        redisTemplate.setHashValueSerializer(hashValueSerializer);
        return redisTemplate;
    }
}
