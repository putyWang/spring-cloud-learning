package com.learning.security.config;

import com.learning.security.annotation.Module;
import com.learning.security.annotation.Permission;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 构造所在服务所有权限信息
 */
@Log4j2
@Component
public class PermissionConfig implements InitializingBean {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private WebApplicationContext applicationContext;

    @Getter
    @Setter
    private Map<String, String> permissionMap = new HashMap<>();

    @Override
    public void afterPropertiesSet(){
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        map.keySet().forEach(mappingInfo -> {
            HandlerMethod handlerMethod = map.get(mappingInfo);
            Permission permission = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Permission.class);
            Module module = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Module.class);
            Optional.ofNullable(permission)
                    .ifPresent(resourcePermission -> mappingInfo
                            .getPatternsCondition()
                            .getPatterns()
                            .forEach(url -> {
                                permissionMap.put(url, String.format("%s:%s:%s", applicationName, module, permission));
                            }));
        });
    }
}
