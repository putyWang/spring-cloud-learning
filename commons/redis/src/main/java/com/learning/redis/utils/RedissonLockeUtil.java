package com.learning.redis.utils;

import com.learning.redis.function.ISynMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author WangWei
 * @version v 1.0
 * @description redisson 分布式锁实现
 * @date 2024-06-21
 **/
@Component
@Log4j2
@RequiredArgsConstructor
public class RedissonLockeUtil {

    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "learning-lock:";

    public RLock lock(String lockKey) {
        RLock lock = this.redissonClient.getFairLock(LOCK_PREFIX + lockKey);
        lock.lock(0L, TimeUnit.SECONDS);
        return lock;
    }

    public void tryLock(String lockKey, int waitTime, int leaseTime, ISynMethod synMethod) {
        try {
            if (tryLock(lockKey, waitTime, leaseTime)) {
                synMethod.invoke();
            } else {
                throw new RuntimeException("锁获取超时");
            }
        } catch (Exception exception) {
            log.error("tryLock:", exception);
        } finally {
            unlock(lockKey);
        }

    }

    public boolean tryLock(String lockKey, int waitTime, int leaseTime) {
        try {
            return redissonClient.getLock(LOCK_PREFIX + lockKey)
                    .tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("tryLock:", e);
        }
        return false;
    }

    public boolean isHeldByCurrentThread(String lockName) {
        return redissonClient.getLock(LOCK_PREFIX + lockName).isHeldByCurrentThread();
    }

    public void unlock(String lockKey) {
        redissonClient.getLock(LOCK_PREFIX + lockKey).unlock();
    }

    public void unlock(RLock lock) {
        lock.unlock();
    }
}
