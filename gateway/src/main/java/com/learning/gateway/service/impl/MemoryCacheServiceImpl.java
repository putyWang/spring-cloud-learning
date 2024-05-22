package com.learning.gateway.service.impl;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.learning.gateway.service.CacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class MemoryCacheServiceImpl implements CacheService {

    private final CacheManager memoryCacheManager;
    private ReentrantReadWriteLock readWriteLock = null;
    private ReentrantReadWriteLock.WriteLock writeLock = null;
    private ReentrantReadWriteLock.ReadLock readLock = null;

    public MemoryCacheServiceImpl(@Qualifier("memory-cache-manager") CacheManager memoryCacheManager) {
        this.memoryCacheManager = memoryCacheManager;
        this.readWriteLock = new ReentrantReadWriteLock();
        this.writeLock = this.readWriteLock.writeLock();
        this.readLock = this.readWriteLock.readLock();
    }

    public void putToCache(String cacheName, String key, Object value) {
        Cache cache = this.memoryCacheManager.getCache(cacheName);
        if (null != cache) {
            this.writeLock.lock();

            try {
                cache.put(key, value);
            } finally {
                this.writeLock.unlock();
            }

        } else {
            String msg = String.format("putToCache 没有找到相应的 cache name:%s", cacheName);
            throw new ServiceException(msg);
        }
    }

    public void updateToCache(String cacheName, String key, Object value) {
        Cache cache = this.memoryCacheManager.getCache(cacheName);
        if (null != cache) {
            this.writeLock.lock();

            try {
                cache.put(key, value);
            } finally {
                this.writeLock.unlock();
            }

        } else {
            String msg = String.format("updateToCache 没有找到相应的 cache name:%s", cacheName);
            throw new ServiceException(msg);
        }
    }

    public String getFromCache(String cacheName, String key) {
        Cache cache = this.memoryCacheManager.getCache(cacheName);
        if (null != cache) {
            this.readLock.lock();

            try {
                Cache.ValueWrapper valueWrapper = cache.get(key);
                Object o;
                if (null != valueWrapper) {
                    o = valueWrapper.get();
                    if (null != o) {
                        String var6 = o.toString();
                        return var6;
                    }
                }

                o = null;
                return (String)o;
            } finally {
                this.readLock.unlock();
            }
        } else {
            String msg = String.format("getFromCache 没有找到相应的 cache name:%s", cacheName);
            throw new ServiceException(msg);
        }
    }

    public Object getObjectFromCache(String cacheName, String key) {
        Cache cache = this.memoryCacheManager.getCache(cacheName);
        if (null != cache) {
            this.readLock.lock();

            Object var5;
            try {
                Cache.ValueWrapper valueWrapper = cache.get(key);
                if (null == valueWrapper) {
                    var5 = null;
                    return var5;
                }

                var5 = valueWrapper.get();
            } finally {
                this.readLock.unlock();
            }

            return var5;
        } else {
            String msg = String.format("getObjectFromCache 没有找到相应的 cache name:%s", cacheName);
            throw new ServiceException(msg);
        }
    }

    public boolean hasKey(String cacheName, String key) {
        Cache cache = this.memoryCacheManager.getCache(cacheName);
        if (null != cache) {
            this.readLock.lock();

            boolean var5;
            try {
                Cache.ValueWrapper valueWrapper = cache.get(key);
                var5 = null != valueWrapper;
            } finally {
                this.readLock.unlock();
            }

            return var5;
        } else {
            String msg = String.format("hasKey 没有找到相应的 cache name:%s", cacheName);
            throw new ServiceException(msg);
        }
    }

    public void evictDbSingleCacheValue(String cacheName, String cacheKey) {
        Cache cache = this.memoryCacheManager.getCache(cacheName);
        if (null != cache) {
            this.writeLock.lock();

            try {
                cache.evict(cacheKey);
            } finally {
                this.writeLock.unlock();
            }

        } else {
            String msg = String.format("evictDbSingleCacheValue 没有找到相应的 cache name:%s", cacheName);
            throw new ServiceException(msg);
        }
    }

    public void evictAllCacheValues(String cacheName) {
        Cache cache = this.memoryCacheManager.getCache(cacheName);
        if (null != cache) {
            this.writeLock.lock();

            try {
                cache.clear();
            } finally {
                this.writeLock.unlock();
            }

        } else {
            String msg = String.format("evictAllCacheValues 没有找到相应的 cache name:%s", cacheName);
            throw new ServiceException(msg);
        }
    }

    public void evictAllCaches() {
        this.memoryCacheManager.getCacheNames().parallelStream().forEach((cacheName) -> {
            Cache cache = this.memoryCacheManager.getCache(cacheName);
            if (null != cache) {
                this.writeLock.lock();

                try {
                    cache.clear();
                } finally {
                    this.writeLock.unlock();
                }
            } else {
                String msg = String.format("evictAllCaches 没有找到相应的 cache name:%s", cacheName);
                log.info(msg);
            }

        });
    }

    public ConcurrentMap<Object, Object> getAll(String cacheName) {
        ConcurrentMapCache cache = (ConcurrentMapCache)this.memoryCacheManager.getCache(cacheName);

        assert cache != null;

        return cache.getNativeCache();
    }
}
