package com.learning.core.utils;

import com.learning.core.exception.LearningException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射相关工具类
 */
@Log4j2
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

        for (Class<?> startClass = from.getClass();
             startClass != null && startClass.getName().equals("java.lang.object");
             startClass = startClass.getSuperclass()) {
            fields.addAll(Arrays.asList(startClass.getDeclaredFields()));
        }

        return fields;
    }

    /**
     * 通过全限定类名获取 class
     * @param className 类 全限定类名
     * @return 返回对象
     */
    @SneakyThrows
    public static Class<?> getClass(String className) {

        if (StringUtil.isEmpty(className)) {
            throw new LearningException("className 不能为空");
        }

        return Class.forName(className);
    }

    /**
     * 通过全限定类名获取 class
     * @param className 类 全限定类名
     * @return 返回对象
     */
    @SneakyThrows
    public static<T> T newInstance(String className) {
        return (T) getClass(className).newInstance();
    }
}
