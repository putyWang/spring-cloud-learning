package com.learning.redis.template;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
@Component
public class RedisRepository {
    private static final Logger log = LoggerFactory.getLogger(RedisRepository.class);
    private static final Charset DEFAULT_CHARSET;
    private static final StringRedisSerializer STRING_SERIALIZER;
    private static final JdkSerializationRedisSerializer OBJECT_SERIALIZER;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RedisRepository() {
    }

    public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setKeySerializer(STRING_SERIALIZER);
        this.redisTemplate.setValueSerializer(OBJECT_SERIALIZER);
    }

    public RedisConnectionFactory getConnectionFactory() {
        return this.redisTemplate.getConnectionFactory();
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

    public void flushDB(RedisClusterNode node) {
        this.redisTemplate.opsForCluster().flushDb(node);
    }

    public void setExpire(final byte[] key, final byte[] value, final long time) {
        this.redisTemplate.execute((connection) -> {
            connection.setEx(key, time, value);
            log.debug("[redisTemplate redis]放入 缓存  url:{} ========缓存时间为{}秒", key, time);
            return 1L;
        });
    }

    public void setExpire(final String key, final Object value, final long time) {
        this.redisTemplate.execute((connection) -> {
            RedisSerializer<String> serializer = this.getRedisSerializer();
            byte[] keys = serializer.serialize(key);
            byte[] values = OBJECT_SERIALIZER.serialize(value);
            connection.setEx(keys, time, values);
            return 1L;
        });
    }

    public void setExpire(final String[] keys, final Object[] values, final long time) {
        this.redisTemplate.execute((connection) -> {
            RedisSerializer<String> serializer = this.getRedisSerializer();

            for(int i = 0; i < keys.length; ++i) {
                byte[] bKeys = serializer.serialize(keys[i]);
                byte[] bValues = OBJECT_SERIALIZER.serialize(values[i]);
                connection.setEx(bKeys, time, bValues);
            }

            return 1L;
        });
    }

    public void set(final String[] keys, final Object[] values) {
        this.redisTemplate.execute((connection) -> {
            RedisSerializer<String> serializer = this.getRedisSerializer();

            for(int i = 0; i < keys.length; ++i) {
                byte[] bKeys = serializer.serialize(keys[i]);
                byte[] bValues = OBJECT_SERIALIZER.serialize(values[i]);
                connection.set(bKeys, bValues);
            }

            return 1L;
        });
    }

    public void set(final String key, final Object value) {
        this.redisTemplate.execute((connection) -> {
            RedisSerializer<String> serializer = this.getRedisSerializer();
            byte[] keys = serializer.serialize(key);
            byte[] values = OBJECT_SERIALIZER.serialize(value);
            connection.set(keys, values);
            log.debug("[redisTemplate redis]放入 缓存  url:{}", key);
            return 1L;
        });
    }

    public List<String> willExpire(final String key, final long time) {
        List<String> keysList = new ArrayList();
        this.redisTemplate.execute((connection) -> {
            Set<String> keys = this.redisTemplate.keys(key + "*");
            Iterator var7 = keys.iterator();

            while(var7.hasNext()) {
                String key1 = (String)var7.next();
                Long ttl = connection.ttl(key1.getBytes(DEFAULT_CHARSET));
                if (0L <= ttl && ttl <= 2L * time) {
                    keysList.add(key1);
                }
            }

            return keysList;
        });
        return keysList;
    }

    public Set<String> keys(final String keyPatten) {
        return (Set)this.redisTemplate.execute((connection) -> {
            return this.redisTemplate.keys(keyPatten + "*");
        });
    }

    public byte[] get(final byte[] key) {
        byte[] result = (byte[])this.redisTemplate.execute((connection) -> {
            return connection.get(key);
        });
        log.debug("[redisTemplate redis]取出 缓存  url:{} ", key);
        return result;
    }

    public Object get(final String key) {
        Object resultStr = this.redisTemplate.execute((connection) -> {
            RedisSerializer<String> serializer = this.getRedisSerializer();
            byte[] keys = serializer.serialize(key);
            byte[] values = connection.get(keys);
            return OBJECT_SERIALIZER.deserialize(values);
        });
        log.debug("[redisTemplate redis]取出 缓存  url:{} ", key);
        return resultStr;
    }

    public Map<String, Object> getKeysValues(final String keyPatten) {
        log.debug("[redisTemplate redis]  getValues()  patten={} ", keyPatten);
        return (Map)this.redisTemplate.execute((connection) -> {
            RedisSerializer<String> serializer = this.getRedisSerializer();
            Map<String, Object> maps = new HashMap(16);
            Set<String> keys = this.redisTemplate.keys(keyPatten + "*");
            if (CollectionUtils.isNotEmpty(keys)) {
                Iterator var6 = keys.iterator();

                while(var6.hasNext()) {
                    String key = (String)var6.next();
                    byte[] bKeys = serializer.serialize(key);
                    byte[] bValues = connection.get(bKeys);
                    Object value = OBJECT_SERIALIZER.deserialize(bValues);
                    maps.put(key, value);
                }
            }

            return maps;
        });
    }

    public HashOperations<String, String, Object> opsForHash() {
        return this.redisTemplate.opsForHash();
    }

    public void putHashValue(String key, String hashKey, Object hashValue) {
        log.debug("[redisTemplate redis]  putHashValue()  key={},hashKey={},hashValue={} ", new Object[]{key, hashKey, hashValue});
        this.opsForHash().put(key, hashKey, hashValue);
    }

    public Object getHashValues(String key, String hashKey) {
        log.debug("[redisTemplate redis]  getHashValues()  key={},hashKey={}", key, hashKey);
        return this.opsForHash().get(key, hashKey);
    }

    public void delHashValues(String key, Object... hashKeys) {
        log.debug("[redisTemplate redis]  delHashValues()  key={}", key);
        this.opsForHash().delete(key, hashKeys);
    }

    public Map<String, Object> getHashValue(String key) {
        log.debug("[redisTemplate redis]  getHashValue()  key={}", key);
        return this.opsForHash().entries(key);
    }

    public void putHashValues(String key, Map<String, Object> map) {
        this.opsForHash().putAll(key, map);
    }

    public long dbSize() {
        return (Long)this.redisTemplate.execute(RedisServerCommands::dbSize);
    }

    public String flushDB() {
        return (String)this.redisTemplate.execute((connection) -> {
            connection.flushDb();
            return "ok";
        });
    }

    public boolean exists(final String key) {
        return (Boolean)this.redisTemplate.execute((connection) -> {
            return connection.exists(key.getBytes(DEFAULT_CHARSET));
        });
    }

    public long del(final String... keys) {
        return (Long)this.redisTemplate.execute((connection) -> {
            long result = 0L;
            String[] var4 = keys;
            int var5 = keys.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String key = var4[var6];
                result = connection.del(new byte[][]{key.getBytes(DEFAULT_CHARSET)});
            }

            return result;
        });
    }

    protected RedisSerializer<String> getRedisSerializer() {
        return this.redisTemplate.getStringSerializer();
    }

    public long incr(final String key) {
        return (Long)this.redisTemplate.execute((connection) -> {
            RedisSerializer<String> redisSerializer = this.getRedisSerializer();
            return connection.incr(redisSerializer.serialize(key));
        });
    }

    public ListOperations<String, Object> opsForList() {
        return this.redisTemplate.opsForList();
    }

    public Long leftPush(String key, Object value) {
        return this.opsForList().leftPush(key, value);
    }

    public Object leftPop(String key) {
        return this.opsForList().leftPop(key);
    }

    public Long in(String key, Object value) {
        return this.opsForList().rightPush(key, value);
    }

    public Object rightPop(String key) {
        return this.opsForList().rightPop(key);
    }

    public Long length(String key) {
        return this.opsForList().size(key);
    }

    public void remove(String key, long i, Object value) {
        this.opsForList().remove(key, i, value);
    }

    public void set(String key, long index, Object value) {
        this.opsForList().set(key, index, value);
    }

    public List<Object> getList(String key, int start, int end) {
        return this.opsForList().range(key, (long)start, (long)end);
    }

    public Long leftPushAll(String key, List<Object> list) {
        return this.opsForList().leftPushAll(key, list);
    }

    public void insert(String key, long index, Object value) {
        this.opsForList().set(key, index, value);
    }

    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
        STRING_SERIALIZER = new StringRedisSerializer();
        OBJECT_SERIALIZER = new JdkSerializationRedisSerializer();
    }
}
