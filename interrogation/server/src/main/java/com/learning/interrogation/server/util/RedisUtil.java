package com.learning.interrogation.server.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedisUtil
 *
 * @Author lihaoru
 * @Date 2021-08-18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> netRedisTemplate;

    // =============================common============================
    public void boundValueOps(String key,List<Map<String, Object>> resultList, long time) {

        try {
                netRedisTemplate.boundValueOps(key).set(resultList, time, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("=====>redis操作：指定缓存失效时间异常："
                    + e.getMessage(), e);
        }

    }
    public Set<String> keys(String key) {

        try {
            Set<String> keys = netRedisTemplate.keys(key);
            return keys;
        } catch (Exception e) {
            log.error("=====>redis操作：指定缓存失效时间异常："
                    + e.getMessage(), e);
        }
        return new HashSet();
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 0
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                netRedisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：指定缓存失效时间异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param key
     * @param time
     * @param timeUnit 时间单位
     * @return {@link boolean}
     * @throws
     * @description 指定缓存失效时间
     * @author lihaoru
     * @date 2021-10-12
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                netRedisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：指定缓存失效时间异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return netRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return netRedisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("=====>redis操作：判断key是否存在异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                netRedisTemplate.delete(key[0]);
            } else {
                netRedisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */

    public Object get(String key) {
        return key == null ? null : netRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取指定类型对象
     *
     * @param key 保存 key
     * @param type 类型
     * @return 指定类型数据或空
     */
    public <T> T get(String key, Class<T> type) {
        Object result = key == null ? null : netRedisTemplate.opsForValue().get(key);
        return result != null && result.getClass().equals(type) ? (T) result : null;
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */

    public String getString(String key) {
        if (key == null) {
            return null;
        }
        Object o = netRedisTemplate.opsForValue().get(key);
        return o == null ? null : String.valueOf(o);
    }

    /**
     * @param key
     * @return {@link String}
     * @throws
     * @description 转string
     * @author liujie
     * @date 12/17/21
     */
    public String getToString(String key) {
        if (key == null) {
            return null;
        }
        Object o = netRedisTemplate.opsForValue().get(key);
        return o == null ? null : String.valueOf(o);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            netRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：普通缓存放入异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 如果不存在就添加
     *
     * @param key   关键字
     * @param value 值
     * @param time  过期时间
     * @return true成功 false失败
     */
    public Boolean setIfAbsent(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            return netRedisTemplate.opsForValue().setIfAbsent(key, value, time, ObjectUtil.defaultIfNull(timeUnit, TimeUnit.MINUTES));
        } catch (Exception e) {
            log.error("=====>redis操作：普通缓存放入异常：" + e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                netRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：普通缓存放入并设置时间异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param key
     * @param value
     * @param time
     * @param timeUnit 时间单位
     * @return {@link boolean}
     * @throws
     * @description 普通缓存放入并设置时间
     * @author lihaoru
     * @date 2021-10-11
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                netRedisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：普通缓存放入并设置时间异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        Assert.isTrue(delta >= 0, "递增因子必须大于0");
        return netRedisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */

    public long decr(String key, long delta) {
        Assert.isTrue(delta >= 0, "递增因子必须大于0");
        return netRedisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Map=================================

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hSet(String key, String item, Object value) {
        try {
            netRedisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：向一张hash表中放入数据异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hSet(String key, String item, Object value, long time) {
        try {
            netRedisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：向一张hash表中放入数据异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public List<Object> hGet(String key, String... item) {
        if (ObjectUtil.isNull(item) || item.length == 0) {
            return new ArrayList<>();
        }

        if (item.length == 1) {
            Object value = netRedisTemplate.opsForHash().get(key, item[0]);
            if (ObjectUtil.isNotNull(value)) {
                return Collections.singletonList(value);
            } else {
                return new ArrayList<>();
            }
        } else {
            return netRedisTemplate.opsForHash().multiGet(key, Arrays.asList(item));
        }
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmGet(String key) {
        return netRedisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public<T> boolean hmSet(String key, Map<String, T> map) {
        try {
            netRedisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：HashSet异常："
                + e.getMessage(), e);
            return false;
        }
    }


    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {

        try {
            netRedisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：HashSet并设置时间异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hDel(String key, Object... item) {
        netRedisTemplate.opsForHash().delete(key, item);
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */

    public boolean hHasKey(String key, String item) {
        return netRedisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hIncr(String key, String item, double by) {
        return netRedisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */

    public double hdecr(String key, String item, double by) {
        return netRedisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return netRedisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("=====>redis操作：将数据放入set缓存异常："
                + e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */

    public Set<Object> sGet(String key) {
        try {
            return netRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("=====>redis操作：根据key获取Set中的所有值异常："
                + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return netRedisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("=====>redis操作：根据value从一个set中查询是否存在异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = netRedisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("=====>redis操作：set数据放入缓存异常："
                + e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return netRedisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("=====>redis操作：获取set缓存的长度异常："
                + e.getMessage(), e);
            return 0;
        }
    }


    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            return netRedisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("=====>redis操作：移除值为value异常："
                + e.getMessage(), e);
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            netRedisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：将list放入缓存异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            netRedisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：将list放入缓存异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            netRedisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：将list放入缓存异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            netRedisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：将list放入缓存异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param key
     * @param value
     * @param time
     * @param timeUnit 时间单位
     * @return {@link boolean}
     * @throws
     * @description 将list放入缓存
     * @author lihaoru
     * @date 2021-10-11
     */
    public boolean lSet(String key, List<Object> value, long time, TimeUnit timeUnit) {
        try {
            netRedisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                netRedisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：将list放入缓存异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return netRedisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("=====>redis操作：获取list缓存的内容异常："
                + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取list缓存尾部内容
     *
     * @param key   键
     * @return
     */
    public Object lEndGet(String key) {
        try {
            return netRedisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("=====>redis操作：获取list缓存的内容异常："
                    + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return netRedisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("=====>redis操作：获取list缓存的长度异常："
                + e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头， 第二个元素，依次类推；index<0时，-，表尾，-倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return netRedisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("=====>redis操作：通过索引获取list中的值异常："
                + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            netRedisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("=====>redis操作：根据索引修改list中的某条数据异常："
                + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            return netRedisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            log.error("=====>redis操作：移除N个值为value：" + e.getMessage(), e);
            return 0;
        }

    }

    // ===============================zset=================================
    /**
     * 更新指定 zset 中指定分数
     *
     * @param key           键
     * @param valueScoreMap 需更新的分数值
     */
    public void updateValues(String key, Map<Object, Double> valueScoreMap, boolean isUpdate) {
        try {
            BoundZSetOperations<String, Object> boundZSetOperations = netRedisTemplate.boundZSetOps(key);
            if (! isUpdate) {
                zSetRemove(key, Long.MIN_VALUE, Long.MAX_VALUE);
            }
            for (Map.Entry<Object, Double> valueScoreEntry : valueScoreMap.entrySet()) {
                if (isUpdate) {
                    boundZSetOperations.incrementScore(valueScoreEntry.getKey(), valueScoreEntry.getValue());
                } else {
                    boundZSetOperations.add(valueScoreEntry.getKey(), valueScoreEntry.getValue());
                }
            }
        } catch (Exception e) {
            log.error("分数更新失败", e);
        }

    }

    /**
     * 获取指定 key 顺序排列所有值
     *
     * @param key 缓存 key
     * @return 排序列表
     */
    public List<String> getOrderListDesc(String key) {
        return getOrderListDesc(key,0L, Long.MAX_VALUE);
    }

    /**
     * 获取 zSet 中指定范围排序列表
     *
     * @param key 缓存 key
     * @param start 开始分数
     * @param end 结束分数
     * @return 排序列表
     */
    public List<String> getOrderListDesc(String key, long start, long end) {
        List<String> result = new ArrayList<>();
        Objects.requireNonNull(netRedisTemplate.boundZSetOps(key).reverseRangeWithScores(start, end))
                .forEach(v -> result.add(String.valueOf(v.getValue())));

        return result;
    }

    /**
     * 获取指定 key 顺序排列所有值（升序）
     *
     * @param key 缓存 key
     * @return 排序列表
     */
    public List<String> getOrderList(String key) {
        return getOrderList(key,0L, Long.MAX_VALUE);
    }

    /**
     * 获取 zSet 中指定范围排序列表（升序）
     *
     * @param key 缓存 key
     * @param start 开始分数
     * @param end 结束分数
     * @return 排序列表
     */
    public List<String> getOrderList(String key, long start, long end) {
        List<String> result = new ArrayList<>();
        Objects.requireNonNull(netRedisTemplate.boundZSetOps(key).rangeByScore(start, end))
                .forEach(v -> result.add(String.valueOf(v)));

        return result;
    }

    /**
     * 获取 zSet 中指定范围排序列表（升序）
     *
     * @param key 缓存 key
     * @param start 开始分数
     * @param end 结束分数
     * @return 排序列表
     */
    public <T extends Object> List<T> getZSetValueList(String key, long start, long end, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        Objects.requireNonNull(netRedisTemplate.boundZSetOps(key).rangeByScore(start, end))
                .forEach(v -> {
                    if (clazz.equals(v.getClass())) {
                        result.add((T) v);
                    }
                });

        return result;
    }

    /**
     * 获取 zSet 是否包含指定元素
     *
     * @param key 缓存 key
     * @param value 元素
     * @return 是否包含指定元素
     */
    public boolean zSetContain(String key, Object value) {
        return zSetScore(key, value) != 0;
    }

    /**
     * 获取 zSet 是否包含指定元素
     *
     * @param key 缓存 key
     * @param value 元素
     * @return 是否包含指定元素
     */
    public double zSetScore(String key, Object value) {
        Assert.notNull(value, "value不能为空");
        return ObjectUtil.defaultIfNull(netRedisTemplate.boundZSetOps(key).score(value), 0D);
    }

    /**
     * 移除 zSet 中指定值列表数据
     *
     * @param key 缓存 key
     * @param valueList 需删除的值列表
     */
    public void zSetRemove(String key, Object... valueList) {
        netRedisTemplate.boundZSetOps(key).remove(valueList);
    }

    /**
     * 移除 zSet 中指定范围数据
     *
     * @param key 缓存 key
     * @param start 开始分数
     * @param end 结束分数
     */
    public void zSetRemove(String key, long start, long end) {
        netRedisTemplate.boundZSetOps(key).removeRange(start, end);
    }

    public Map<Object, Double> zSetGetAll(String key) {
        BoundZSetOperations<String, Object> stringObjectBoundZSetOperations = netRedisTemplate.boundZSetOps(key);
        Map<Object, Double> result = new HashMap<>();
        Objects.requireNonNull(stringObjectBoundZSetOperations.reverseRangeWithScores(Long.MIN_VALUE, Long.MAX_VALUE)).forEach(
                value -> result.put(value.getValue(), value.getScore())
        );
        return result;
    }
}
