package com.learning.validation.aspect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.ValidatorUtils;
import com.learning.validation.annotation.YHValid;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.xml.bind.ValidationException;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
@Aspect
public class ValidationAspect {
    private static final int MAX_VALID_LEVEL = 8;

    public ValidationAspect() {
    }

    @Pointcut("@within(com.learning.validation.annotation.YHValidParam) || @annotation(com.learning.validation.annotation.YHValidParam)")
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
            if (arg != null && (param.getAnnotation(YHValid.class) != null || arg.getClass().getAnnotation(YHValid.class) != null)) {
                if (arg instanceof Collection) {
                    this.validateCollection(arg);
                } else if (arg instanceof JSONObject) {
                    this.validateMap(arg);
                } else {
                    this.validateEntity(arg);
                }
            }
        }

    }

    private void validateMap(Object arg) {
        this.validateMap(arg, 0);
    }

    private void validateMap(Object arg, int level) {
        JSONObject jsonObject = (JSONObject)arg;

        for (Object value : jsonObject.values()) {
            validateEntity(value, level);
        }

    }

    private void validateCollection(Object arg) {
        this.validateCollection(arg, 0);
    }

    private void validateCollection(Object arg, int level) {
        Collection collection = (Collection)arg;
        Iterator var4 = collection.iterator();

        while(var4.hasNext()) {
            Object item = var4.next();
            this.validateEntity(item, level);
        }

    }

    private void validateEntity(Object arg) {
        this.validateEntity(arg, 0);
    }

    private void validateEntity(Object arg, int level) {
        if (level < 8) {
            try {
                if (arg != null) {
                    ValidatorUtils.validateEntity(arg, new Class[0]);
                    ++level;
                    this.validFields(arg, level);
                }
            } catch (ServiceException var5) {
                ValidationException ve = new ValidationException(var5.getMsg(), var5);
                ve.setData(var5.getData());
                throw ve;
            }
        }
    }

    private void validFields(Object arg, int level) {
        Class paramClazz = arg.getClass();
        Field[] fields = paramClazz.getDeclaredFields();
        if (fields != null && fields.length != 0) {
            Field[] var5 = fields;
            int var6 = fields.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Field f = var5[var7];
                if (this.fieldNeedValid(f)) {
                    f.setAccessible(true);
                    Object fieldValue = ReflectionUtils.getField(f, arg);
                    if (fieldValue instanceof Collection) {
                        this.validateCollection(fieldValue, level);
                    } else if (fieldValue instanceof Map) {
                        this.validateMap(fieldValue, level);
                    } else {
                        this.validateEntity(fieldValue, level);
                    }
                }
            }

        }
    }

    private boolean fieldNeedValid(Field f) {
        if (f.isAnnotationPresent(YHValid.class)) {
            return true;
        } else {
            Class fieldClass = f.getType();
            return fieldClass.getAnnotation(YHValid.class) != null;
        }
    }
}

