package com.learning.gateway.service;

public interface CacheService {
    void putToCache(String cacheName, String key, Object value);

    void updateToCache(String cacheName, String key, Object value);

    String getFromCache(String cacheName, String key);

    Object getObjectFromCache(String cacheName, String key);

    boolean hasKey(String cacheName, String key);

    void evictDbSingleCacheValue(String cacheName, String cacheKey);

    void evictAllCacheValues(String cacheName);

    void evictAllCaches();
}
