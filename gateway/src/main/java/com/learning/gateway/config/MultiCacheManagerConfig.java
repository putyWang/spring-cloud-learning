package com.learning.gateway.config.MultiCacheManagerConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class MultiCacheManagerConfig {

    public MultiCacheManagerConfig() {
    }

    public CacheManager getMemoryCacheManager() {
        ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
        List<String> list = Arrays.asList("services", "service_apis", "service_api_timeout", "service_rate_limiter", "service_load_balance", "blacks", "api_whites", "whites", "tripartite_auth_code", "same_client_login_config");
        concurrentMapCacheManager.setCacheNames(list);
        return concurrentMapCacheManager;
    }

    @Bean({"redis-cache-manager"})
    @Primary
    public CacheManager getRedisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues();
        Set<String> cacheNames = new HashSet(Arrays.asList("services", "service_apis", "service_api_timeout", "service_rate_limiter", "service_load_balance", "blacks", "api_whites", "whites", "tripartite_auth_code", "routes", "same_client_login_config"));
        Map<String, RedisCacheConfiguration> configMap = new HashMap();

        cacheNames.forEach(cacheName -> {
            configMap.put(cacheName, config);
        });

        return RedisCacheManager.builder(factory).initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();
    }
}
