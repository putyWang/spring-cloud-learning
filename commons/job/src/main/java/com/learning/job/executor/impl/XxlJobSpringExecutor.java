package com.learning.job.executor.impl;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.glue.GlueFactory;
import com.learning.job.handler.IJobHandler;
import com.learning.job.handler.annotation.JobHandler;
import com.learning.job.handler.annotation.XxlJob;
import com.learning.job.handler.impl.MethodJobHandler;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import com.learning.job.executor.XxlJobExecutor;

@Log4j2
public class XxlJobSpringExecutor
        extends XxlJobExecutor
        implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {
    private static ApplicationContext applicationContext;

    public XxlJobSpringExecutor() {
    }

    public void afterSingletonsInstantiated() {
        this.initJobHandlerRepository(applicationContext);
        this.initJobHandlerMethodRepository(applicationContext);
        GlueFactory.refreshInstance(1);

        try {
            super.start();
        } catch (Exception var2) {
            Exception e = var2;
            throw new RuntimeException(e);
        }
    }

    private void initJobHandlerRepository(ApplicationContext applicationContext) {
        if (applicationContext != null) {
            Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);
            if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
                Iterator var3 = serviceBeanMap.values().iterator();

                while(var3.hasNext()) {
                    Object serviceBean = var3.next();
                    if (serviceBean instanceof IJobHandler) {
                        String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                        IJobHandler handler = (IJobHandler)serviceBean;
                        if (loadJobHandler(name) != null) {
                            throw new RuntimeException("yh-job jobhandler naming conflicts.");
                        }

                        registJobHandler(name, handler);
                    }
                }
            }

        }
    }

    private void initJobHandlerMethodRepository(ApplicationContext applicationContext) {
        if (applicationContext != null) {
            String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
            String[] var3 = beanDefinitionNames;
            int var4 = beanDefinitionNames.length;

            label86:
            for(int var5 = 0; var5 < var4; ++var5) {
                String beanDefinitionName = var3[var5];
                Object bean = applicationContext.getBean(beanDefinitionName);
                Map<Method, XxlJob> annotatedMethods = null;

                try {
                    annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(), new MethodIntrospector.MetadataLookup<XxlJob>() {
                        public XxlJob inspect(Method method) {
                            return AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class);
                        }
                    });
                } catch (Throwable var19) {
                    Throwable ex = var19;
                    log.error("yh-job method-jobhandler resolve error for bean[" + beanDefinitionName + "].", ex);
                }

                if (annotatedMethods != null && !annotatedMethods.isEmpty()) {
                    Iterator var20 = annotatedMethods.entrySet().iterator();

                    while(true) {
                        Method method;
                        XxlJob xxlJob;
                        do {
                            if (!var20.hasNext()) {
                                continue label86;
                            }

                            Map.Entry<Method, XxlJob> methodXxlJobEntry = (Map.Entry)var20.next();
                            method = methodXxlJobEntry.getKey();
                            xxlJob = methodXxlJobEntry.getValue();
                        } while(xxlJob == null);

                        String name = xxlJob.value();
                        if (name.trim().length() == 0) {
                            throw new RuntimeException("yh-job method-jobhandler name invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                        }

                        if (loadJobHandler(name) != null) {
                            throw new RuntimeException("yh-job jobhandler[" + name + "] naming conflicts.");
                        }

                        if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].isAssignableFrom(String.class)) {
                            throw new RuntimeException("yh-job method-jobhandler param-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , The correct method format like \" public ReturnT<String> execute(String param) \" .");
                        }

                        if (!method.getReturnType().isAssignableFrom(ReturnT.class)) {
                            throw new RuntimeException("yh-job method-jobhandler return-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , The correct method format like \" public ReturnT<String> execute(String param) \" .");
                        }

                        method.setAccessible(true);
                        Method initMethod = null;
                        Method destroyMethod = null;
                        if (xxlJob.init().trim().length() > 0) {
                            try {
                                initMethod = bean.getClass().getDeclaredMethod(xxlJob.init());
                                initMethod.setAccessible(true);
                            } catch (NoSuchMethodException var18) {
                                throw new RuntimeException("yh-job method-jobhandler initMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                            }
                        }

                        if (xxlJob.destroy().trim().length() > 0) {
                            try {
                                destroyMethod = bean.getClass().getDeclaredMethod(xxlJob.destroy());
                                destroyMethod.setAccessible(true);
                            } catch (NoSuchMethodException var17) {
                                throw new RuntimeException("yh-job method-jobhandler destroyMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                            }
                        }

                        registJobHandler(name, new MethodJobHandler(bean, method, initMethod, destroyMethod));
                    }
                }
            }

        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        XxlJobSpringExecutor.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}

