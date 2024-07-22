package com.learning.redis.start;

import com.learning.redis.config.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author WangWei
 * @version v 1.0
 * @description redis 链接验证器
 * @date 2024-06-18
 **/
@Component
@RequiredArgsConstructor
@Log4j2
public class RedisConnectionCheck implements CommandLineRunner {

    private final RedisTemplate<?, ?> redisTemplate;

    private RedisProperties redisProperties;

    @Override
    public void run(String... args) {
        try {
            Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        } catch (Exception e) {
            log.error("redis connection failed !  连接失败", e);
            if (redisProperties.isThrowErr()) {
                throw new RuntimeException(e);
            }
        }
    }
}
