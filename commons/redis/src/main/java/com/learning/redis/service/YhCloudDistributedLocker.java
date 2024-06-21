package com.learning.redis.service;

import org.redisson.api.RLock;

/**
 * @author WangWei
 * @version v 1.0
 * @description 分布式锁接口
 * @date 2024-06-18
 **/
public interface YhCloudDistributedLocker {
    /**
     * 上锁
     * @param lockKey 锁 key
     * @return 锁对象
     */
    RLock lock(String lockKey);

    /**
     * 尝试获取锁，并在取得锁后制定 synMethod 方法
     * @param lockKey 锁 key
     * @param waitTime 超时时间
     * @param leaseTime 锁保持时间
     * @param synMethod 执行方法对象
     */
    void tryLock(String lockKey, int waitTime, int leaseTime, ISynMethod synMethod);

    /**
     * 尝试取得锁
     * @param lockKey 锁 key
     * @param waitTime 超时时间
     * @param leaseTime 锁保持时间
     * @return 是否取得锁
     */
    boolean tryLock(String lockKey, int waitTime, int leaseTime);

    /**
     * 当前是否本进程持有锁
     * @param lockName 锁 key
     * @return 是否持有锁
     */
    boolean isHeldByCurrentThread(String lockName);

    /**
     * 解锁
     * @param lockKey 锁 key
     */
    void unlock(String lockKey);

    /**
     * 解锁
     * @param lock 锁 对象
     */
    void unlock(RLock lock);
}
