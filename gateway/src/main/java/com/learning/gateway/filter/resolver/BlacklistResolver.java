package com.learning.gateway.filter.resolver;

import com.learning.gateway.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BlacklistResolver {
    private static final Logger log = LoggerFactory.getLogger(BlacklistResolver.class);

    @Autowired
    @Qualifier("redis-cache-service")
    private CacheService cacheService;

    public BlacklistResolver() {
    }

    public boolean existBlacklist(String key) {
        try {
            return this.cacheService.hasKey("blacks", key);
        } catch (Exception var3) {
            if (log.isDebugEnabled()) {
                log.error("black list has key invoke error:{}", key);
            }

            return false;
        }
    }
}
