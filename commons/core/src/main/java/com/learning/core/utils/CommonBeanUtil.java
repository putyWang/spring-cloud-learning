package com.learning.core.utils;

import com.learning.core.domain.annotation.DtoSkip;
import com.learning.core.domain.annotation.FormatterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * bean操作类
 */
public class CommonBeanUtil {

    /**
     * 记录本类中的日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(CommonBeanUtil.class);

    private CommonBeanUtil() {
    }

    /**
     * 将元列表转化为目标列表
     *
     * @param sourceList
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {

        return sourceList.stream().map((source) -> {
            try {
                T target = targetClass.newInstance();
                copyAndFormat(target, source);
                return target;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    /**
     * 将元对象转化为目标对象
     *
     * @param targetClass
     * @param source
     * @param <T>
     * @return
     */
    public static <T> T copyAndFormat(Class<T> targetClass, Object source) {
        try {
            T target = targetClass.newInstance();
            copyAndFormat(target, source);
            return target;
        } catch (Exception e) {
            throw new RuntimeException(String.format("对象转换错误：%s->%s", source.getClass().getName(), targetClass.getName()), e);
        }
    }

    /**
     * 将元对象转化为目标对象
     *
     * @param target
     * @param source
     * @param <T>
     * @return
     */
    public static <T> T copyAndFormat(T target, Object source) {

        //获取目标类中所有属性
        Field[] declaredFields = target.getClass().getDeclaredFields();
        //将相应属性添加到相应集合中
        List<String> dtoSkipFields = new ArrayList<>();
        List<Field> formatterTypeFields = new ArrayList<>();

        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];

            if (field.isAnnotationPresent(DtoSkip.class)) {
                dtoSkipFields.add(field.getName());
            } else if (field.isAnnotationPresent(FormatterType.class)) {
                formatterTypeFields.add(field);
            }
        }

        //将所有标准化程序字段设置为跳过
        dtoSkipFields.addAll(formatterTypeFields.stream().map(Field::getName).collect(Collectors.toList()));
        //将source元对象中除忽略属性以外的属性值赋予目标对象
        BeanUtils.copyProperties(source, target, dtoSkipFields.toArray(new String[0]));
        //为target对象所有值进行赋值
        for (Field field : formatterTypeFields) {

            try {
                //获取source对象用相应属性
                PropertyDescriptor sourcePropertyDescriptor = new PropertyDescriptor(field.getName(), source.getClass());
                //获取target对象用相应属性
                PropertyDescriptor targetPropertyDescriptor = new PropertyDescriptor(field.getName(), target.getClass());
                //获取source对象用对应属性值
                Object fieldSource = sourcePropertyDescriptor.getReadMethod().invoke(source);

                if (fieldSource != null) {
                    Method writeMethod = targetPropertyDescriptor.getWriteMethod();
                    //为target对象用相应属性赋值
                    switch (field.getAnnotation(FormatterType.class).type()) {
                        case OBJECT:
                            Object fieldTarget = field.getType().newInstance();
                            copyAndFormat(fieldSource, fieldTarget);
                            writeMethod.invoke(target, fieldTarget);
                            break;
                        case LIST:
                            Type genericType = field.getGenericType();
                            ParameterizedType parameterizedType = (ParameterizedType) genericType;
                            Class<?> fieldTargetClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            List<?> fieldTargetList = copyList((List<?>) fieldSource, fieldTargetClass);
                            writeMethod.invoke(target, fieldTargetList);
                    }
                }

            } catch (Exception e) {
                logger.error("FormatterType处理异常", e);
            }
        }

        return target;
    }

    public static String formatKey(String key, String[] replaceArray, Map<String, Object> params, Object source) {
        if (key.contains("${")) {
            Map<String, Object> keyPatternMap = new HashMap<>(2);

            for (String fieldName : replaceArray) {
                try {
                    Object value = params.get(fieldName);
                    if (null == value) {
                        Field declaredField = source.getClass().getDeclaredField(fieldName);
                        declaredField.setAccessible(true);
                        value = declaredField.get(source);
                    }

                    keyPatternMap.put(fieldName, value);
                } catch (Exception var11) {
                }
            }

            key = CommonUtils.replaceFormatString(key, keyPatternMap);
            if (key.contains("${")) {
                return null;
            }
        }

        return key;
    }

    /**
     * 初始化泛型类
     *
     * @param cls 泛型类对象
     * @param <T> 泛型
     * @return
     */
    public static <T> T getNewObject(Class<T> cls) {
        T t = null;
        try {
            t = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }
}
