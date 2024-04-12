package com.learning.core.utils;

import java.lang.reflect.Array;
import java.util.*;

public class ObjectUtils {
    public ObjectUtils() {
    }

    /**
     * 判断对象是否为null
     *
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj) {
        return null == obj;
    }

    /**
     * 判断对象是否不为null
     *
     * @param obj
     * @return
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * 判断对象是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof CharSequence) {
            return 0 == ((CharSequence) obj).length();
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return 0 == Array.getLength(obj);
        } else if (obj instanceof Optional) {
            return !((Optional) obj).isPresent();
        } else if (obj instanceof Iterator) {
            return !((Iterator) obj).hasNext();
        } else {
            return false;
        }
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断数组中元素是否全为空
     *
     * @param values
     * @return
     */
    public static boolean allNotNull(Object... values) {
        if (values == null) {
            return false;
        } else {

            for (Object val : values) {
                if (val == null) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * 若对象为空，返回默认对象
     *
     * @param value
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return null == value ? defaultValue : value;
    }

    /**
     * 判断对象是否相等
     *
     * @param o1
     * @param o2
     * @return
     */
    public static boolean equal(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }

    /**
     * 判断对象是否不相等
     *
     * @param o1
     * @param o2
     * @return
     */
    public static boolean notEqual(Object o1, Object o2) {
        return !equal(o1, o2);
    }

    /**
     * 获取数组hash值
     *
     * @param objects
     * @return
     */
    public static int hashCode(Object... objects) {
        return Arrays.hashCode(objects);
    }

    /**
     * 获取数组相关对象的长度
     *
     * @param obj
     * @return
     */
    public static int length(Object obj) {
        if (null == obj) {
            return 0;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length();
        } else if (obj instanceof Collection) {
            return ((Collection) obj).size();
        } else if (obj instanceof Map) {
            return ((Map) obj).size();
        } else {
            return obj.getClass().isArray() ? Array.getLength(obj) : -1;
        }
    }

    public static <T extends Comparable<? super T>> int compare(T t1, T t2) {
        return compare(t1, t2, false);
    }

    public static <T extends Comparable<? super T>> int compare(T t1, T t2, boolean nullGreater) {
        if (t1 == t2) {
            return 0;
        } else if (t1 == null) {
            return nullGreater ? 1 : -1;
        } else if (t2 == null) {
            return nullGreater ? -1 : 1;
        } else {
            return t1.compareTo(t2);
        }
    }
}
