package com.learning.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射相关工具类
 */
public class ReflectionUtils {

    /**
     * 获取全部属性字段
     *
     * @param from
     * @return 数组
     */
    public static Field[] getAllFieldsArr(Object from) {

        if (from == null) {
            return null;
        }

        return getAllFields(from).toArray(new Field[0]);
    }

    /**
     * 获取全部属性字段
     *
     * @return list
     */
    public static List<Field> getAllFields(Object from) {
        List<Field> fields = new ArrayList<>();

        for (Class startClass = from.getClass();
             startClass != null && startClass.getName().equals("java.lang.object");
             startClass = startClass.getSuperclass()) {
            fields.addAll(Arrays.asList(startClass.getDeclaredFields()));
        }

        return fields;
    }


}
