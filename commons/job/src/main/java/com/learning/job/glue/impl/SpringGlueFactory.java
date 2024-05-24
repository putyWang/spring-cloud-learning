package com.learning.job.glue.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.annotation.Resource;

import com.learning.job.executor.impl.XxlJobSpringExecutor;
import com.learning.job.glue.GlueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;

public class SpringGlueFactory extends GlueFactory {
    private static Logger logger = LoggerFactory.getLogger(SpringGlueFactory.class);

    public SpringGlueFactory() {
    }

    public void injectService(Object instance) {
        if (instance != null) {
            if (XxlJobSpringExecutor.getApplicationContext() != null) {
                Field[] fields = instance.getClass().getDeclaredFields();
                Field[] var3 = fields;
                int var4 = fields.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Field field = var3[var5];
                    if (!Modifier.isStatic(field.getModifiers())) {
                        Object fieldBean = null;
                        if (AnnotationUtils.getAnnotation(field, Resource.class) != null) {
                            try {
                                Resource resource = (Resource)AnnotationUtils.getAnnotation(field, Resource.class);
                                if (resource.name() != null && resource.name().length() > 0) {
                                    fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(resource.name());
                                } else {
                                    fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(field.getName());
                                }
                            } catch (Exception var11) {
                            }

                            if (fieldBean == null) {
                                fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(field.getType());
                            }
                        } else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
                            Qualifier qualifier = (Qualifier)AnnotationUtils.getAnnotation(field, Qualifier.class);
                            if (qualifier != null && qualifier.value() != null && qualifier.value().length() > 0) {
                                fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(qualifier.value());
                            } else {
                                fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(field.getType());
                            }
                        }

                        if (fieldBean != null) {
                            field.setAccessible(true);

                            try {
                                field.set(instance, fieldBean);
                            } catch (IllegalArgumentException var9) {
                                logger.error(var9.getMessage(), var9);
                            } catch (IllegalAccessException var10) {
                                logger.error(var10.getMessage(), var10);
                            }
                        }
                    }
                }

            }
        }
    }
}
