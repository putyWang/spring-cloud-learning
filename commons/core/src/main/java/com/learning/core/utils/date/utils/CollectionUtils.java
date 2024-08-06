package com.learning.core.utils.date.utils;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 */
public class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {

        return collection == null || collection.isEmpty();
    }

    /**
     * 判断Map是否为空
     *
     * @param maps
     * @return
     */
    public static boolean isEmpty(Map<?, ?> maps) {
        return maps == null || maps.isEmpty();
    }

    /**
     * 判断Map是否不为空
     *
     * @param maps
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> maps) {
        return !isEmpty(maps);
    }
}
