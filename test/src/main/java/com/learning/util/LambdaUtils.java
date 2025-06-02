package com.learning.util;

import cn.hutool.core.collection.CollUtil;
import com.learning.conditions.interfaces.SFunction;
import com.learning.entity.metadata.*;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Locale.ENGLISH;

/**
 * Lambda 解析工具类
 *
 * @author HCL, MieMie
 * @since 2018-05-10
 */
public final class LambdaUtils {

    /**
     * 字段映射
     */
    private static final Map<String, Map<String, ColumnCache>> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> LambdaMeta extract(SFunction<T, ?> func) {
        // 1. IDEA 调试模式下 lambda 表达式是一个代理
        if (func instanceof Proxy) {
            return new IdeaProxyLambdaMeta((Proxy) func);
        }
        // 2. 反射读取
        try {
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            return new ReflectLambdaMeta((java.lang.invoke.SerializedLambda)method.invoke(func), func.getClass().getClassLoader());
        } catch (Throwable e) {
            // 3. 反射失败使用序列化的方式读取
            return new ShadowLambdaMeta(SerializedLambda.extract(func));
        }
    }

    /**
     * 格式化 key 将传入的 key 变更为大写格式
     * 为了支持首字母是大写的字段
     *
     * <pre>
     *     Assert.assertEquals("USERID", formatKey("userId"))
     * </pre>
     *
     * @param key key
     * @return 大写的 key
     */
    public static String formatKey(String key) {
        return key.toUpperCase(ENGLISH);
    }

    /**
     * 缓存实体字段 MAP 信息
     *
     * @param info 表信息
     * @return 缓存 map
     */
    private static Map<String, ColumnCache> createColumnCacheMap(Class<?> info) {
        Map<String, ColumnCache> map;
        map = new HashMap<>(info.getDeclaredFields().length);
        Arrays.stream(info.getDeclaredFields()).forEach(field ->
                map.put(formatKey(field.getName()), new ColumnCache(field.getName(), field.getName()))
        );
        return map;
    }

    /**
     * 获取实体对应字段 MAP
     *
     * @param clazz 实体类
     * @return 缓存 map
     */
    public static Map<String, ColumnCache> getColumnMap(Class<?> clazz) {
        return COLUMN_CACHE_MAP.computeIfAbsent(clazz.getName(), key -> createColumnCacheMap(clazz));
    }

}
