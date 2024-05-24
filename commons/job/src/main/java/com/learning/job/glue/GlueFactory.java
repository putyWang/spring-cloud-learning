package com.learning.job.glue;

import com.learning.job.glue.impl.SpringGlueFactory;
import com.learning.job.handler.IJobHandler;
import groovy.lang.GroovyClassLoader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GlueFactory {
    private static GlueFactory glueFactory = new GlueFactory();
    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
    private ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap();

    public GlueFactory() {
    }

    public static GlueFactory getInstance() {
        return glueFactory;
    }

    public static void refreshInstance(int type) {
        if (type == 0) {
            glueFactory = new GlueFactory();
        } else if (type == 1) {
            glueFactory = new SpringGlueFactory();
        }

    }

    public IJobHandler loadNewInstance(String codeSource) throws Exception {
        if (codeSource != null && codeSource.trim().length() > 0) {
            Class<?> clazz = this.getCodeSourceClass(codeSource);
            if (clazz != null) {
                Object instance = clazz.newInstance();
                if (instance != null) {
                    if (instance instanceof IJobHandler) {
                        this.injectService(instance);
                        return (IJobHandler)instance;
                    }

                    throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, cannot convert from instance[" + instance.getClass() + "] to IJobHandler");
                }
            }
        }

        throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
    }

    private Class<?> getCodeSourceClass(String codeSource) {
        try {
            byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes());
            String md5Str = (new BigInteger(1, md5)).toString(16);
            Class<?> clazz = (Class)this.CLASS_CACHE.get(md5Str);
            if (clazz == null) {
                clazz = this.groovyClassLoader.parseClass(codeSource);
                this.CLASS_CACHE.putIfAbsent(md5Str, clazz);
            }

            return clazz;
        } catch (Exception var5) {
            return this.groovyClassLoader.parseClass(codeSource);
        }
    }

    public void injectService(Object instance) {
    }
}
