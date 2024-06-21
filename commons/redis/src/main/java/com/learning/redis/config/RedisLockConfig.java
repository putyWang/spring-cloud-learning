package com.learning.redis.config;

import lombok.extern.log4j.Log4j2;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author WangWei
 * @description redis 分布式锁配置
 * @date 2024-06-18
 * @version v 1.0
 **/
@ComponentScan({"com.yanhua.cloud.redislock"})
@Configuration
@Log4j2
public class RedisLockConfig {
    @Autowired
    private YhCloudDefaultRedisConfig redisProperties;

    @Bean
    RedissonClient redissonClient() {
        RedisConfiguration redisConfiguration = this.redisProperties.redisConfiguration(this.redisProperties.getDatabase());
        Config config = new Config();
        if (redisConfiguration instanceof RedisSentinelConfiguration) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            sentinelServersConfig.setMasterName(this.redisProperties.getMaster());
            Set<RedisNode> sentinels = ((RedisSentinelConfiguration)redisConfiguration).getSentinels();
            String[] nodes = new String[sentinels.size()];
            ((List)sentinels.stream().map((t) -> "redis://" + t.getHost() + ":" + t.getPort()).collect(Collectors.toList())).toArray(nodes);
            sentinelServersConfig.addSentinelAddress(nodes);
            if (null != this.redisProperties.getDatabase()) {
                sentinelServersConfig.setDatabase(this.redisProperties.getDatabase());
            }

            if (!StringUtils.isEmpty(this.redisProperties.getPassword())) {
                sentinelServersConfig.setPassword(this.redisProperties.getPassword());
            }

            sentinelServersConfig.setMasterConnectionMinimumIdleSize(this.redisProperties.getMasterMinSize());
            sentinelServersConfig.setSlaveConnectionMinimumIdleSize(this.redisProperties.getSlaveMinSize());
            return Redisson.create(config);
        } else if (redisConfiguration instanceof RedisClusterConfiguration) {
            Set<RedisNode> clusterNodes = ((RedisClusterConfiguration)redisConfiguration).getClusterNodes();
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            clusterServersConfig.addNodeAddress((String[])clusterNodes.toArray());
            if (!StringUtils.isEmpty(this.redisProperties.getPassword())) {
                clusterServersConfig.setPassword(this.redisProperties.getPassword());
            }

            return Redisson.create(config);
        } else {
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setAddress("redis://" + this.redisProperties.getHost() + ":" + this.redisProperties.getPort());
            if (null != this.redisProperties.getDatabase()) {
                singleServerConfig.setDatabase(this.redisProperties.getDatabase());
            }

            if (!StringUtils.isEmpty(this.redisProperties.getPassword())) {
                singleServerConfig.setPassword(this.redisProperties.getPassword());
            }

            singleServerConfig.setConnectionMinimumIdleSize(this.redisProperties.getMasterMinSize());
            return Redisson.create(config);
        }
    }

    @PostConstruct
    public void init() {
        log.info("yh-redislock-{} init ..", RedisLockConfig.class.getPackage().getImplementationVersion());
    }
}
