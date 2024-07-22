package com.learning.redis.utils;

import cn.hutool.core.util.ArrayUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author WangWei
 * @version v 1.0
 * @description redis 工具类
 * @date 2024-06-18
 **/
public class RedisUtil {

    @Getter
    private final RedisTemplate<String, Object> redisTemplate;

    private final int maxWait;

    String lua_script = " local rlist = redis.call('lrange' , KEYS[1],  ARGV[1], ARGV[2]\n)  redis.call('ltrim', KEYS[1], #rlist,-1)\n return rlist\n";

    DefaultRedisScript<List<String>> defaultRedisScript = new DefaultRedisScript<>();

    public RedisUtil(RedisTemplate<String, Object> redisTemplate, int maxWait) {
        this.defaultRedisScript.setScriptText(this.lua_script);
        this.defaultRedisScript.setResultType(List.class);
        this.redisTemplate = redisTemplate;
        this.maxWait = maxWait;
    }

    public long incr(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递增因子必须大于0");
        } else {
            return this.redisTemplate.opsForValue().increment(key, delta);
        }
    }

    public long decr(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递减因子必须大于0");
        } else {
            return this.redisTemplate.opsForValue().increment(key, -delta);
        }
    }

    /**
     * 设置过期时间
     *
     * @param key key 值
     * @param timeSeconds 过期时间，以秒为单位
     * @return
     */
    public boolean expire(String key, long timeSeconds) {
        try {
            if (timeSeconds > 0L) {
                this.redisTemplate.expire(key, timeSeconds, TimeUnit.SECONDS);
            }

            return true;
        } catch (Exception var5) {
            var5.printStackTrace();
            return false;
        }
    }

    /**
     * 获取过期时间
     *
     * @param key key 值
     * @return
     */
    public long getExpire(String key) {
        return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 是否存在指定 key
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        try {
            return this.redisTemplate.hasKey(key);
        } catch (Exception var3) {
            var3.printStackTrace();
            return false;
        }
    }

    /**
     * 是否存在指定key 列表
     *
     * @param keys key 列表
     * @return
     */
    public Long hasKeys(Collection<String> keys) {
        try {
            return this.redisTemplate.countExistingKeys(keys);
        } catch (Exception var3) {
            var3.printStackTrace();
            return -1L;
        }
    }

    /**
     * 查询指定前缀的所有 key
     *
     * @param keyPrefix key 前缀
     * @return
     */
    public Set<String> keys(String keyPrefix) {
        return this.redisTemplate.keys(keyPrefix + "*");
    }

    /**
     * 删除指定 key
     *
     * @param keys key 数组
     */
    public void delete(String... keys) {
        if (ArrayUtil.isEmpty(keys)) {
            return;
        }

        if (keys.length == 1) {
            this.redisTemplate.delete(keys[0]);
        } else {
            this.redisTemplate.delete(Arrays.asList(keys));
        }

    }

    /**
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        try {
            return key == null ? null : this.redisTemplate.opsForValue().get(key);
        } catch (Exception var3) {
            var3.getMessage();
            return null;
        }
    }

    public boolean put(String key, Object value) {
        return this.put(key, value, 0L);
    }

    public boolean put(String key, Object value, long timeSeconds) {
        return this.put(key, value, timeSeconds, TimeUnit.SECONDS);
    }

    public boolean put(String key, Object value, long time, TimeUnit unit) {
        try {
            if (time > 0L) {
                this.redisTemplate.opsForValue().set(key, value, time, unit);
            } else {
                this.redisTemplate.opsForValue().set(key, value);
            }

            return true;
        } catch (Exception var7) {
            var7.printStackTrace();
            return false;
        }
    }

    public Map<Object, Object> getMap(String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    public boolean putMap(String key, Map<String, Object> map) {
        return this.putMap(key, map, 0L);
    }

    public boolean putMap(String key, Map<String, Object> map, long timeSeconds) {
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            if (timeSeconds > 0L) {
                this.expire(key, timeSeconds);
            }

            return true;
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public Object getHash(String key, Object hashKey) {
        return this.redisTemplate.opsForHash().get(key, hashKey);
    }

    public boolean putHash(String key, String hashKey, Object value) {
        return this.putHash(key, hashKey, value, 0L);
    }

    public boolean putHash(String key, String hashKey, Object value, long timeSeconds) {
        try {
            this.redisTemplate.opsForHash().put(key, hashKey, value);
            if (timeSeconds > 0L) {
                this.expire(key, timeSeconds);
            }

            return true;
        } catch (Exception var7) {
            var7.printStackTrace();
            return false;
        }
    }

    public void delHash(String key, Object... item) {
        this.redisTemplate.opsForHash().delete(key, item);
    }

    public boolean hasKeyByHash(String key, String item) {
        return this.redisTemplate.opsForHash().hasKey(key, item);
    }

    public double incrHash(String key, String item, double by) {
        return this.redisTemplate.opsForHash().increment(key, item, by);
    }

    public double decrHash(String key, String item, double by) {
        return this.redisTemplate.opsForHash().increment(key, item, -by);
    }

    public Set<Object> getSet(String key) {
        try {
            return this.redisTemplate.opsForSet().members(key);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public boolean hasKeyBySet(String key, Object value) {
        try {
            return this.redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }
    }

    public long putSet(String key, Object... values) {
        return this.putSet(key, 0L, values);
    }

    public long putSet(String key, long timeSeconds, Object... values) {
        try {
            Long count = this.redisTemplate.opsForSet().add(key, values);
            if (timeSeconds > 0L) {
                this.expire(key, timeSeconds);
            }

            return count;
        } catch (Exception var6) {
            var6.printStackTrace();
            return 0L;
        }
    }

    public long getSetSize(String key) {
        try {
            return this.redisTemplate.opsForSet().size(key);
        } catch (Exception var3) {
            var3.printStackTrace();
            return 0L;
        }
    }

    public long delSet(String key, Object... values) {
        try {
            return this.redisTemplate.opsForSet().remove(key, values);
        } catch (Exception var4) {
            var4.printStackTrace();
            return 0L;
        }
    }

    public long getSizeByList(String key) {
        try {
            return this.redisTemplate.opsForList().size(key);
        } catch (Exception var3) {
            var3.printStackTrace();
            return 0L;
        }
    }

    public List<Object> getList(String key, long start, long end) {
        try {
            return this.redisTemplate.opsForList().range(key, start, end);
        } catch (Exception var7) {
            var7.printStackTrace();
            return new ArrayList();
        }
    }

    public List<Object> popList(String key, long start, long end) {
        try {
            List<String> keys = new ArrayList();
            keys.add(key);
            List result = (List)this.redisTemplate.execute(this.defaultRedisScript, keys, new Object[]{start, end});
            return result;
        } catch (Exception var8) {
            var8.printStackTrace();
            return new ArrayList();
        }
    }

    public Object getLisetByIndex(String key, long index) {
        try {
            return this.redisTemplate.opsForList().index(key, index);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public boolean putToLeft(String key, Object value) {
        return this.putPrivate(key, value, 0L, true);
    }

    public boolean putToLeft(String key, Object value, long timeSeconds) {
        return this.putPrivate(key, value, timeSeconds, true);
    }

    public boolean putToRight(String key, Object value) {
        return this.putPrivate(key, value, 0L, false);
    }

    public boolean putToRight(String key, Object value, long timeSeconds) {
        return this.putPrivate(key, value, timeSeconds, false);
    }

    private boolean putPrivate(String key, Object value, long time, boolean isLeft) {
        try {
            if (isLeft) {
                this.redisTemplate.opsForList().leftPush(key, value);
            } else {
                this.redisTemplate.opsForList().rightPush(key, value);
            }

            if (time > 0L) {
                this.expire(key, time);
            }

            return true;
        } catch (Exception var7) {
            var7.printStackTrace();
            return false;
        }
    }

    public boolean putListToLeft(String key, List<Object> value) {
        return this.putListPrivate(key, value, 0L, true);
    }

    public boolean putListToLeft(String key, List<Object> value, long timeSeconds) {
        return this.putListPrivate(key, value, timeSeconds, true);
    }

    public boolean putListToRight(String key, List<Object> value) {
        return this.putListPrivate(key, value, 0L, false);
    }

    public boolean putListToRight(String key, List<Object> value, long timeSeconds) {
        return this.putListPrivate(key, value, timeSeconds, false);
    }

    private boolean putListPrivate(String key, List<Object> value, long time, boolean isLeft) {
        try {
            if (isLeft) {
                this.redisTemplate.opsForList().leftPushAll(key, value);
            } else {
                this.redisTemplate.opsForList().rightPushAll(key, value);
            }

            if (time > 0L) {
                this.expire(key, time);
            }

            return true;
        } catch (Exception var7) {
            var7.printStackTrace();
            return false;
        }
    }

    public boolean updateListByIndex(String key, long index, Object value) {
        try {
            this.redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public long delList(String key, long count, Object value) {
        try {
            Long remove = this.redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception var6) {
            var6.printStackTrace();
            return 0L;
        }
    }

    public Object leftPop(String key, long timeOutSeconds) {
        if (timeOutSeconds <= 0L) {
            timeOutSeconds = (long)this.maxWait;
        }

        try {
            return this.redisTemplate.opsForList().leftPop(key, timeOutSeconds, TimeUnit.MILLISECONDS);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public Object rightPop(String key, long timeOutSeconds) {
        if (timeOutSeconds <= 0L) {
            timeOutSeconds = (long)this.maxWait;
        }

        try {
            return this.redisTemplate.opsForList().rightPop(key, timeOutSeconds, TimeUnit.MILLISECONDS);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }
}
