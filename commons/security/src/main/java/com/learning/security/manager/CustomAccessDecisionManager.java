package com.learning.security.manager;

import com.learning.core.utils.CollectionUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException {
        // 如果这个url 没有配置权限配置拒绝策略
        if(CollectionUtils.isEmpty(collection)) {
            throw new AccessDeniedException("没有权限访问");
        }

        // 2.匹配对应权限
        for(ConfigAttribute c : collection) {
            String needPermission = c.getAttribute();
            for(GrantedAuthority ga : authentication.getAuthorities()) {
                if(needPermission.trim().equals(ga.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("没有权限访问");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
