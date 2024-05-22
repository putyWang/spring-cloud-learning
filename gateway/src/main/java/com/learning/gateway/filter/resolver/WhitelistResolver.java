package com.learning.gateway.filter.resolver;

import java.util.concurrent.ConcurrentLinkedDeque;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class WhitelistResolver {

    public static ConcurrentLinkedDeque<String> whiteListQeque = new ConcurrentLinkedDeque();

    @Autowired
    @Qualifier("redis-cache-service")
    private CacheService cacheService;

    public WhitelistResolver() {
    }

    public boolean existWhitelist(String key) {
        try {
            return this.cacheService.hasKey("whites", key);
        } catch (Exception var3) {
            if (log.isDebugEnabled()) {
                log.error("white list has key invoke error:{}", key);
            }

            return false;
        }
    }

    public boolean existApiWhitelist(String key) {
        try {
            return whiteListQeque.contains(key);
        } catch (Exception var3) {
            if (log.isDebugEnabled()) {
                log.error("api white list has key invoke error:{}", key);
            }

            return false;
        }
    }
}
