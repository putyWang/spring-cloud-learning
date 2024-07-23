package com.learning.redis.config;

import com.learning.core.utils.StringUtil;
import com.learning.redis.config.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;

import javax.annotation.PostConstruct;

/**
 * @author WangWei
 * @version v 1.0
 * @description redis 分布式锁配置
 * @date 2024-06-18
 **/
@Configuration
@Log4j2
@ConditionalOnProperty(
        name = "learning.cloud.redis.lock.enable",
        havingValue = "true"
)
public class RedisLockConfig {

    @Bean
    RedissonClient redissonClient(RedisProperties redisProperties, RedisConfiguration redisConfiguration) {
        Config config = new Config();
        // 1 构建守卫模式配置
        if (redisConfiguration instanceof RedisSentinelConfiguration) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .addSentinelAddress(
                            ((RedisSentinelConfiguration) redisConfiguration).getSentinels()
                                    .stream().map((t) -> DefaultRedisConfig.REDIS_PREFIX + t.getHost() + ":" + t.getPort())
                                    .toArray(String[]::new)
                    ).setDatabase(redisProperties.getDatabase())
                    .setMasterConnectionMinimumIdleSize(redisProperties.getLettuce().getMasterMinSize())
                    .setSlaveConnectionMinimumIdleSize(redisProperties.getLettuce().getSlaveMinSize());

            if (StringUtil.isNotBlank(redisProperties.getPassword())) {
                sentinelServersConfig.setPassword(redisProperties.getPassword());
            }
            // 2 构建集群配置
        } else if (redisConfiguration instanceof RedisClusterConfiguration) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers()
                    .addNodeAddress(
                            ((RedisClusterConfiguration) redisConfiguration).getClusterNodes().stream()
                                    .map((t) -> DefaultRedisConfig.REDIS_PREFIX + t.getHost() + ":" + t.getPort())
                                    .toArray(String[]::new)
                    );

            if (StringUtil.isNotBlank(redisProperties.getPassword())) {
                clusterServersConfig.setPassword(redisProperties.getPassword());
            }
            // 3 构建单机配置
        } else {
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig
                    .setAddress(DefaultRedisConfig.REDIS_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setDatabase(redisProperties.getDatabase())
                    .setConnectionMinimumIdleSize(redisProperties.getLettuce().getMasterMinSize());

            if (StringUtil.isNotBlank(redisProperties.getPassword())) {
                singleServerConfig.setPassword(redisProperties.getPassword());
            }
        }

        return Redisson.create(config);
    }

    @PostConstruct
    public void init() {
        log.info("redisLock-{} init ..", getClass().getPackage().getImplementationVersion());
    }
}
