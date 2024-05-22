package com.learning.security.service;

import cn.hutool.core.util.ArrayUtil;
import com.learning.security.model.LearningUser;
import com.learning.core.constants.*;
import com.learning.core.model.UserContext;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户服务
 */
public abstract class CustomUserDetailService implements UserDetailsService, Ordered {

    private CacheManager cacheManager;

    /**
     * 是否支持此客户端校验
     *
     * @param clientId 目标客户端
     * @return true/false
     */
    public boolean support(String clientId, String grantType) {
        return true;
    }

    /**
     * 排序值 默认取最大的
     * @return 排序值
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    /**
     * 构建userdetails
     *
     * @param username 用户名
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        // 1.从缓存获取用户信息
        Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
        if (cache != null && cache.get(username) != null) {
            return (LearningUser) cache.get(username).get();
        }

        UserContext userContext = getUserInfoByName(username);
        // 2.获取权限列表
        Set<String> dbAuthsSet = new HashSet<>();
        if (ArrayUtil.isNotEmpty(userContext.getRoles())) {
            // 根据角色添加用户权限列表
            userContext.getRoles().forEach(role -> dbAuthsSet.addAll(role.getPermissionList()));

        }

        Collection<GrantedAuthority> authorities = AuthorityUtils
                .createAuthorityList(dbAuthsSet.toArray(new String[0]));
        UserDetails userDetails = new LearningUser(userContext.getUserId(), userContext.getAccount(),
                SecurityConstants.BCRYPT + userContext.getPassword(), userContext.getPhone(), true, true, true,
                userContext.getStatus() == CommonConstants.STATUS_NORMAL, authorities);
        // 2.向缓存中添加用户信息
        if (cache != null) {
            cache.put(username, userDetails);
        }
        return userDetails;
    }

    /**
     * 实际查询用户信息方法
     * @param username 用户名
     * @return
     */
    public abstract UserContext getUserInfoByName(String username);

    /**
     * 通过用户实体查询
     *
     * @param pigUser user
     * @return
     */
    public abstract UserDetails loadUserByUser(LearningUser pigUser);
}
