package com.learning.redis.utils;

import com.learning.core.utils.ObjectUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
@Component
@NoArgsConstructor
@RequiredArgsConstructor
@Log4j2
public class RedisUtil {
    private static final Charset DEFAULT_CHARSET;
    private static final StringRedisSerializer STRING_SERIALIZER;
    private static final JdkSerializationRedisSerializer OBJECT_SERIALIZER;

    /**
     * 永不过期 key 存储时间值
     */
    private static final long NO_EXPIRE_TIME_VALUE = 0L;

    /**
     * keys 通配符查询表达式
     */
    private static final String WILDCARD_CHARACTER_PARTTERN = "%s*";

    /**
     * redis 模板
     */
    @Getter
    private RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setKeySerializer(STRING_SERIALIZER);
        this.redisTemplate.setValueSerializer(OBJECT_SERIALIZER);
    }

    public RedisConnectionFactory getConnectionFactory() {
        return this.redisTemplate.getConnectionFactory();
    }

    /**
     * 按指定过期时间保存 key value 键值对
     * @param key key 字节数组
     * @param value value 字节数组
     * @param time 过期时间，以秒为单位 <= 0 不设置过期时间
     */
    public void setExpire(final byte[] key, final byte[] value, final long time) {
        this.redisTemplate.execute((RedisCallback<Object>) connection -> {
            if (Boolean.TRUE.equals(time <= 0 ? connection.set(key, value) : connection.setEx(key, time, value))) {
                log.info("[redisTemplate redis]放入 缓存  url:{} ========缓存时间为{}秒", key, time);
            }
            return 1L;
        });
    }

    /**
     * 按指定过期时间保存 key value 键值对
     * @param key key 字符串
     * @param value value 对象
     * @param time 过期时间，以秒为单位 <= 0 不设置过期时间
     */
    public void setExpire(final String key, final Object value, final long time) {
        setExpire(getRedisSerializer().serialize(key), OBJECT_SERIALIZER.serialize(value), time);
    }

    /**
     * 批量按指定过期时间保存 key value 键值对数组
     * @param keys key 字符串数组
     * @param values value 对象数组
     * @param time 过期时间，以秒为单位 <= 0 不设置过期时间
     */
    public void setExpire(final String[] keys, final Object[] values, final long time) {
        for(int i = 0; i < keys.length; ++i) {
            setExpire(keys[i], values[i], time);
        }
    }

    /**
     * 向 redis 中新增 key value 键值对
     * @param keys key 字符串数组
     * @param values value 对象数组
     */
    public void set(final String[] keys, final Object[] values) {
        setExpire(keys, values, NO_EXPIRE_TIME_VALUE);
    }

    /**
     * 保存 key value 键值对
     * @param key key 字符串
     * @param value value 对象
     */
    public void set(final String key, final Object value) {
        setExpire(key, value, NO_EXPIRE_TIME_VALUE);
    }

    /**
     * 设置过期时间
     *
     * @param key key 值
     * @param timeSeconds 过期时间，以秒为单位
     */
    public void expire(String key, long timeSeconds) {
        if (timeSeconds > NO_EXPIRE_TIME_VALUE) {
            this.redisTemplate.expire(key, timeSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取过期时间
     *
     * @param key key 值
     * @return 过期时间 单位 秒
     */
    public long getExpire(String key) {
        return ObjectUtils.defaultIfNull(redisTemplate.getExpire(key, TimeUnit.SECONDS), 0L);
    }


    /**
     * 查询指定前缀的所有 key
     * @param keyPrefix key 前缀
     * @return 匹配的 key 列表
     */
    public Set<String> keys(final String keyPrefix) {
        return this.redisTemplate.keys(getKeyPatten(keyPrefix));
    }

    /**
     * 是否存在指定 key
     * @param key key 字符串
     * @return 是否存在指定 key
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 是否存在指定key 列表
     * @param keys key 列表
     * @return 存在 key 的个数
     */
    public Long hasKeys(Collection<String> keys) {
        return redisTemplate.countExistingKeys(keys);
    }

    /**
     * 查询指定前缀且将要在指定时间内过期的 key 集合
     * @param keyPrefix 前缀
     * @param time 时间 单位 秒
     * @return 匹配的key 列表
     */
    public List<String> willExpire(final String keyPrefix, final long time) {
        Set<String> keySet = keys(keyPrefix);

        if (CollectionUtils.isEmpty(keySet)) {
            return new ArrayList<>();
        }

        return keySet.stream().filter(key ->
                redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                    Long ttl = connection.ttl(key.getBytes(DEFAULT_CHARSET));
                    return ttl != null && 0L <= ttl && ttl <= time;
        })).collect(Collectors.toList());
    }

    /**
     * 获取指定 key 对应的字节数组
     * @param key key 字节数组
     * @return value 字节数组
     */
    public byte[] get(final byte[] key) {
        return redisTemplate.execute((RedisCallback<byte[]>)connection -> connection.get(key));
    }

    /**
     * 获取指定 key 对应的对象
     * @param key key 字符串
     * @return value 对象
     */
    public Object get(final String key) {
        return OBJECT_SERIALIZER.deserialize(get(getRedisSerializer().serialize(key)));
    }

    /**
     * 获取指定前缀的所有key value 键值对
     * @param keyPrefix key 前缀
     * @return 键值对
     */
    public Map<String, Object> getKeysValues(final String keyPrefix) {
        log.debug("[redisTemplate redis]  getValues()  patten={} ", keyPrefix);

        Set<String> keySet = keys(keyPrefix);

        if (CollectionUtils.isEmpty(keySet)) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        keySet.forEach(key -> result.put(key, get(key)));
        return result;
    }

    /**
     * 获取 hash 操作对象
     * @return hash 操作对象
     */
    public HashOperations<String, String, Object> opsForHash() {
        return redisTemplate.opsForHash();
    }

    /**
     * 向指定 hash 新增元素
     *
     * @param key key 字符串
     * @param hashKey hash 中的 key 字符串
     * @param hashValue hash 中对应的值
     */
    public void putHashValue(String key, String hashKey, Object hashValue) {
        opsForHash().put(key, hashKey, hashValue);
    }

    /**
     * 获取 hash 中指定值
     *
     * @param key key 字符串
     * @param hashKey hash 中的 key 字符串
     */
    public Object getHashValues(String key, String hashKey) {
        return opsForHash().get(key, hashKey);
    }

    /**
     * 批量删除指定 hash 中的 hash key
     * @param key hash 对应 key 字符串
     * @param hashKeys hash 会被删除的 key 数组
     */
    public void delHashValues(String key, Object... hashKeys) {
        opsForHash().delete(key, hashKeys);
    }

    /**
     * 获取指定 key 对应的 hash
     * @param key key 字符串
     * @return 对应的 map
     */
    public Map<String, Object> getHashValue(String key) {
        return opsForHash().entries(key);
    }

    /**
     * 新增指定hash
     * @param key key 字符串
     * @param map hash 对应 map
     */
    public void putHashValues(String key, Map<String, Object> map) {
        opsForHash().putAll(key, map);
    }

    /**
     *
     * @param key
     * @param map
     * @param timeSeconds
     * @return
     */
    public void putMap(String key, Map<String, Object> map, long timeSeconds) {
        putHashValues(key, map);
        expire(key, timeSeconds);
    }

    /**
     * 查看当前 数据库 大小
     * @return 数据库大小
     */
    public Long dbSize() {
        return redisTemplate.execute(RedisServerCommands::dbSize);
    }

    /**
     * 刷新所有 db
     * @return 执行结果
     */
    public Boolean flushDB() {
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.flushDb();
            return true;
        });
    }

    /**
     * 刷新指定节点 db
     * @param node 节点参数
     */
    public void flushDB(RedisClusterNode node) {
        redisTemplate.opsForCluster().flushDb(node);
    }


    /**
     * 是否已经存在指定 key 的数据
     * @param key key 字符串
     * @return 是否存在
     */
    public boolean exists(final String key) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.exists(key.getBytes(DEFAULT_CHARSET))));
    }

    /**
     * 批量删除 key
     * @param keys key 字符串数组
     * @return 成功删除的条数
     */
    public Long del(final String... keys) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            long result = 0L;
            for (String key : keys) {
                result += ObjectUtils.defaultIfNull(connection.del(new byte[][]{key.getBytes(DEFAULT_CHARSET)}), 0L);
            }
            return result;
        });
    }

    /**
     * 原子加 1
     * @param key 字符串
     * @return 增加后的值
     */
    public Long incr(final String key) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> ObjectUtils.defaultIfNull(connection.incr(Objects.requireNonNull(getRedisSerializer().serialize(key))), 0L));
    }

    /**
     * 按步长增加指定值
     * @param key 字符串
     * @return 增加后的值
     */
    public Long incrBy(final String key, final long value) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> ObjectUtils.defaultIfNull(connection.incrBy(Objects.requireNonNull(getRedisSerializer().serialize(key)), value), 0L));
    }

    /**
     * 原子减 1
     * @param key 字符串
     * @return 增加后的值
     */
    public Long decr(String key, long value) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> ObjectUtils.defaultIfNull(connection.decr(Objects.requireNonNull(getRedisSerializer().serialize(key))), 0L));

    }

    /**
     * 按步长减少指定值
     * @param key 字符串
     * @return 增加后的值
     */
    public Long decrBy(final String key, final long value) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> ObjectUtils.defaultIfNull(connection.decrBy(Objects.requireNonNull(getRedisSerializer().serialize(key)), value), 0L));
    }

    /**
     * 获取 list 操作对象
     * @return list 操作对象
     */
    public ListOperations<String, Object> opsForList() {
        return redisTemplate.opsForList();
    }

    /**
     * 从左边插入元素
     * @param key list 对应 key
     * @param value 将要插入的元素
     * @return 当前list的长度
     */
    public Long leftPush(String key, Object value) {
        return opsForList().leftPush(key, value);
    }

    /**
     * 获取左边的第一个元素
     * @param key list 对应 key
     * @return 元素对象
     */
    public Object leftPop(String key) {
        return opsForList().leftPop(key);
    }

    /**
     * 从右边插入元素
     * @param key list 对应 key
     * @param value 将要插入的元素
     * @return 当前list的长度
     */
    public Long rightPush(String key, Object value) {
        return opsForList().rightPush(key, value);
    }

    /**
     * 获取右边的第一个元素
     * @param key list 对应 key
     * @return 元素对象
     */
    public Object rightPop(String key) {
        return opsForList().rightPop(key);
    }

    /**
     * 获取指定 list 长度
     * @param key list 对应 key
     * @return 长度
     */
    public Long length(String key) {
        return opsForList().size(key);
    }

    /**
     * 移除 list 中指定索引的元素
     * @param key list 对应 key
     * @param index 索引
     * @param value 值
     */
    public void remove(String key, long index, Object value) {
        opsForList().remove(key, index, value);
    }

    /**
     * 在 list 中指定索引位置处设置元素
     * @param key list 对应 key
     * @param index 索引
     * @param value 值
     */
    public void set(String key, long index, Object value) {
        opsForList().set(key, index, value);
    }

    /**
     * 获取 list 中指定子数组
     * @param key list 对应 key
     * @param start 起始索引
     * @param end 结束索引
     * @return 子数组
     */
    public List<Object> getList(String key, int start, int end) {
        return opsForList().range(key, start, end);
    }

    /**
     * 从左边插入所有元素
     * @param key list 对应 key
     * @param list 将要插入的元素列表
     * @return 当前list的长度
     */
    public Long leftPushAll(String key, List<Object> list) {
        return opsForList().leftPushAll(key, list);
    }

    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
        STRING_SERIALIZER = new StringRedisSerializer();
        OBJECT_SERIALIZER = new JdkSerializationRedisSerializer();
    }

    /**
     * 获取 redis 序列化器
     * @return redis 序列化器
     */
    private RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }

    /**
     * 获取指定 key 前缀的通配符
     * @param keyPrefix key 前缀
     * @return 通配查询表达式
     */
    private static String getKeyPatten (String keyPrefix) {
        return String.format(WILDCARD_CHARACTER_PARTTERN, keyPrefix);
    }
}
