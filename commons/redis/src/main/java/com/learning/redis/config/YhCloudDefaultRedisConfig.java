package com.learning.redis.config;

import com.learning.redis.utils.RedisUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
@Configuration("yhCloudRedisConfig")
@Log4j2
@Data
public class YhCloudDefaultRedisConfig extends YhCloudAbstractRedisConfig {
    @Value("${yanhua.cloud.redis.database:0}")
    private Integer database = null;

    @Bean({"yhCloudRedisTemplate"})
    public RedisTemplate<String, Object> redisTemplate() {
        return super.yhCloudDefaultRedisTemplate(this.database);
    }

    @Bean({"yhCloudRedisUtil"})
    @Order(-2147483646)
    public RedisUtil getYhCloudRedisUtil(@Qualifier("yhCloudRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        return new RedisUtil(redisTemplate, this.timeout);
    }

    @PostConstruct
    public void init() {
        log.info("yh-redis-{} init ..", YhCloudDefaultRedisConfig.class.getPackage().getImplementationVersion());
    }
}
