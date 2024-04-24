package com.learning.web.config.commandLineRunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 程序启动时扫描所有带日志注解的接口
 */
@Slf4j
@Component
public class LogScanCommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
//        List<String> result = new ArrayList<>();
//        WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
//
//        // 获取所有的RequestMapping
//        Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext,
//                HandlerMapping.class, true, false);
//
//        for (HandlerMapping handlerMapping : allRequestMappings.values()) {
//            // 只需要RequestMappingHandlerMapping中的URL映射
//            if (handlerMapping instanceof RequestMappingHandlerMapping) {
//                RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) handlerMapping;
//                Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
//                for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : handlerMethods.entrySet()) {
//                    HandlerMethod mappingInfoValue = requestMappingInfoHandlerMethodEntry.getValue();
//                    //选择有RequiresPermissions注解的
//                    if (mappingInfoValue.getMethod().isAnnotationPresent(SysLog.class)) {
//                        SysLog log = mappingInfoValue.getMethod().getAnnotation(SysLog.class);
//
//                        if (log != null) {
//                            //获取注解中的值
//                            String values = log.value();
//                            result.addAll(Arrays.asList(values));
//                        }
//                    }
//                }
//            }
//        }
//        result = result.stream().distinct().collect(Collectors.toList());
//        return R.success(result);
    }
}
