package com.learning.security.metadata;

import cn.hutool.core.text.AntPathMatcher;
import com.learning.security.config.PermissionConfig;
import com.learning.security.config.properties.PermitAllUrlProperties;
import com.learning.core.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private PermissionConfig permissionConfig;

    private PermitAllUrlProperties permitAllUrlProperties;

    public CustomFilterInvocationSecurityMetadataSource(PermissionConfig permissionConfig, PermitAllUrlProperties permitAllUrlProperties) {
        this.permissionConfig = permissionConfig;
        this.permitAllUrlProperties = permitAllUrlProperties;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        String url = fi.getRequestUrl();

        List<String> ignoreUrls = permitAllUrlProperties.getUrls();

        if(CollectionUtils.isNotEmpty(ignoreUrls) && ignoreUrls.contains(url)){
            // 如果是忽略认证的直接放行
            return null;
        }

        Map<String, String> permissionMap = permissionConfig.getPermissionMap();
        for (String permissionUrl : permissionMap.keySet()){
            if(antPathMatcher.match(permissionUrl, url)){
                return SecurityConfig.createList(permissionMap.get(permissionUrl));
            }
        }
        //如果这个url 没有配置权限 直接返回空
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
