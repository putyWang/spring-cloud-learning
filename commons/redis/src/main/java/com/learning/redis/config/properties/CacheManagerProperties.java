package com.learning.redis.config.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author WangWei
 * @description
 * @date 2024-06-21
 **/
@Setter
@Getter
public class CacheManagerProperties {

    private List<CacheConfig> configs;

    @Setter
    @Getter
    public static class CacheConfig {
        private String key;
        private long second = 60L;
    }
}
