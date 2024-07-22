package com.learning.validation.aspect;

import com.alibaba.fastjson.JSONObject;
import com.learning.validation.annotation.Valid;
import com.learning.validation.core.exception.ValidException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author WangWei
 * @version v 1.0
 * @description 参数验证切面
 * @date 2024-06-21
 **/
@Aspect
public class ValidationAspect {

    /**
     * 验证器对象
     */
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 最大能验证层级
     */
    private static final int MAX_VALID_LEVEL = 8;

    public ValidationAspect() {
    }

    @Pointcut("@within(com.learning.validation.annotation.ValidParam) || @annotation(com.learning.validation.annotation.ValidParam)")
    public void validClassPointCut() {
    }

    @Before("validClassPointCut()")
    public void validationParam(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Parameter[] params = signature.getMethod().getParameters();

        for(int i = 0; i < params.length; ++i) {
            Parameter param = params[i];
            Object arg = args[i];
            if (arg != null && (param.isAnnotationPresent(Valid.class) || arg.getClass().isAnnotationPresent(Valid.class))) {
                if (arg instanceof Collection) {
                    this.validateCollection((Collection<?>)arg);
                } else if (arg instanceof JSONObject) {
                    this.validateMap((JSONObject)arg);
                } else {
                    this.validateEntity(arg);
                }
            }
        }

    }

    /**
     * Map 验证
     *
     * @param arg 值
     */
    private void validateMap(JSONObject arg) {
        this.validateMap(arg, 0);
    }

    /**
     * Map 验证
     *
     * @param arg 值
     * @param level 层级
     */
    private void validateMap(Map<?, ?> arg, int level) {
        for (Object value : arg.values()) {
            validateEntity(value, level);
        }

    }

    /**
     * 集合验证
     *
     * @param arg 值
     */
    private void validateCollection(Collection<?> arg) {
        this.validateCollection(arg, 0);
    }

    /**
     * 集合验证
     *
     * @param arg 值
     * @param level 层级
     */
    private void validateCollection(Collection<?> arg, int level) {
        for (Object item : arg) {
            this.validateEntity(item, level);
        }
    }

    /**
     * 对象验证
     *
     * @param arg 值
     */
    private void validateEntity(Object arg) {
        this.validateEntity(arg, 0);
    }

    /**
     * 对象验证
     *
     * @param arg 值
     * @param level 层级
     */
    private void validateEntity(Object arg, int level) {
        if (level < MAX_VALID_LEVEL) {
            if (arg != null) {
                validateEntity(arg, new Class[0]);
                ++level;
                validFields(arg, level);
            }
        }
    }

    /**
     * 字段验证
     *
     * @param arg 值
     * @param level 层级
     */
    private void validFields(Object arg, int level) {
        Arrays.stream(arg.getClass().getDeclaredFields())
                // 1 过滤需要验证字段
                .filter(this::fieldNeedValid)
                .forEach(field -> {
                    // 2 获取字段值
                    field.setAccessible(true);
                    Object fieldValue = ReflectionUtils.getField(field, arg);
                    // 3 字段验证
                    // 3.1 集合验证
                    if (fieldValue instanceof Collection) {
                        this.validateCollection((Collection<?>)fieldValue, level);
                    // 3.2 map 验证
                    } else if (fieldValue instanceof Map) {
                        this.validateMap((Map<?, ?>)fieldValue, level);
                    // 3.3 对象验证
                    } else {
                        this.validateEntity(fieldValue, level);
                    }
                });
    }

    /**
     * 字段是否需要验证
     * @param f 字段对象
     * @return 是否需要验证
     */
    private boolean fieldNeedValid(Field f) {
        return f.isAnnotationPresent(Valid.class) || f.getType().getAnnotation(Valid.class) != null;
    }

    /**
     * 对象验证
     *
     * @param object 需验证对象
     * @param groups 需校验的分组（会验证其父组的约束）
     * @throws ValidException 验证失败后抛出的异常
     */
    private static void validateEntity(Object object, Class<?>... groups) throws ValidException {
        // 1 调用验证器对对象进行验证
        Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(object, groups);
        // 2 遍历处理验证结果
        if (! constraintViolations.isEmpty()) {
            throw new ValidException("数据校验错误", constraintViolations.stream().
                    collect(
                            Collectors.toMap(
                                    constraintViolation -> constraintViolation.getPropertyPath().toString(),
                                    ConstraintViolation::getMessage
                            )
                    ));
        }
    }
}

